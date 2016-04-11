package org.programmiersportgruppe.scala.commons.basics

import org.scalatest.{DiagrammedAssertions, FunSuite}

import scala.concurrent.{Await, ExecutionContext, Future}

class FuturesTest extends FunSuite with DiagrammedAssertions {

  import Futures._
  import scala.concurrent.duration._
  import ExecutionContext.Implicits.global

  test("handleFailure-action should be triggered by failure") {
    val futureResult: Future[String] = Future.failed(new RuntimeException("FAIL"))

    var collector: List[String] = List.empty

    val optionalFutureResult: Future[Option[String]] = futureResult
      .handleFailure((ex) => {
        collector = collector :+ ex.getMessage
      })
      .toOption

    val optionalResult = Await.result(optionalFutureResult, 2.seconds)

    assert(optionalResult == None)
    assert(collector == List("FAIL"))
  }

  test("handleFailure-action should not be triggered in success case and successful value propagated") {
    val futureResult: Future[String] = Future.successful("Successful result")

    var collector: List[String] = List.empty

    val optionalFutureResult: Future[Option[String]] = futureResult
      .handleFailure((ex) => {
        collector = collector :+ ex.getMessage
      })
      .toOption

    val optionalResult = Await.result(optionalFutureResult, 2.seconds)

    assert(collector == List())
    assert(optionalResult == Some("Successful result"))
  }

}
