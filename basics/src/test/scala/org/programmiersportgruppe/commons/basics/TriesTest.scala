package org.programmiersportgruppe.commons.basics

import org.scalatest.{DiagrammedAssertions, FunSuite}

import scala.util.{Failure, Success, Try}

class TriesTest extends FunSuite with DiagrammedAssertions {
    import Tries._

    test("onFailure-action should be triggered by failure") {
        val potentialResult: Try[String] = Failure(new RuntimeException("FAIL"))

        var collector: List[String] = List.empty

        val optionalResult: Option[String] = potentialResult
            .onFailure((ex) => {collector = collector :+ ex.getMessage})
            .toOption

        assert(optionalResult == None)
        assert(collector == List("FAIL"))
    }

    test("onFailure-action should not be triggered in success case and successful value propagated") {
        val potentialResult: Try[String] = Success("Successful result")

        var errorCollector: List[String] = List.empty

        val optionalResult: Option[String] = potentialResult
        .onFailure((ex) => {errorCollector = errorCollector :+ ex.getMessage})
        .toOption

        assert(errorCollector == List())
        assert(optionalResult == Some("Successful result"))
    }

}
