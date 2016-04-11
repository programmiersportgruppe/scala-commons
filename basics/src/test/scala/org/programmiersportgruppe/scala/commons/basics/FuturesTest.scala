package org.programmiersportgruppe.scala.commons.basics

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.exceptions.TestFailedException
import org.scalatest.{DiagrammedAssertions, FunSuite}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

class FuturesTest extends FunSuite with DiagrammedAssertions with ScalaFutures {

  import Futures._
  import scala.concurrent.duration._
  import ExecutionContext.Implicits.global

  test("mapTry wraps successful future result in Success") {
    val future = Future.successful(42)
      .mapTry {
        case Success(42) => "Foo"
        case _ => "Boo"
      }

    val result = future.futureValue

    assert(result === "Foo")
  }

  test("mapTry replaces failed future with successful future containing Failed(_)") {
    val exception = new RuntimeException("Whoops!")
    val future =
      Future.failed[Int](exception)
        .mapTry {
          case Failure(`exception`) => "Bar"
          case _ => "Boo"
        }

    val result = future.futureValue

    assert(result === "Bar")
  }

  test("mapTry fails the future with exceptions thrown during mapping") {
    val exception = new RuntimeException("Whoops!")
    val future =
      Future.successful(42)
        .mapTry(_ => throw exception)

    val result = intercept[TestFailedException] {
      future.futureValue
    }

    assert(result.cause == Some(exception))
  }

  test("flatMapTry wraps successful future result in Success") {
    val future =
      Future.successful(42)
        .flatMapTry {
          case Success(42) => Future.successful("Foo")
          case _ => Future.successful("Boo")
        }

    val result = future.futureValue

    assert(result === "Foo")
  }

  test("flatMapTry replaces failed future with successful future containing Failed(_)") {
    val exception = new RuntimeException("Whoops!")
    val future =
      Future.failed[Int](exception)
        .flatMapTry {
          case Failure(`exception`) => Future.successful("Bar")
          case _ => Future.successful("Boo")
        }

    val result = future.futureValue

    assert(result === "Bar")
  }

  test("flatMapTry fails the future with a returned failed future") {
    val exception = new RuntimeException("Whoops!")
    val future =
      Future.successful(42)
        .flatMapTry(_ => Future.failed(exception))

    val result = intercept[TestFailedException] {
      future.futureValue
    }

    assert(result.cause == Some(exception))
  }

  test("flatMapTry fails the future with exceptions thrown during mapping") {
    val exception = new RuntimeException("Whoops!")
    val future =
      Future.successful(42)
        .flatMapTry(_ => throw exception)

    val result = intercept[TestFailedException] {
      future.futureValue
    }

    assert(result.cause == Some(exception))
  }

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
