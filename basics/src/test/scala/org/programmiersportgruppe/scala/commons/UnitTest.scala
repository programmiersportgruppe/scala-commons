package org.programmiersportgruppe.scala.commons

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{DiagrammedAssertions, FunSuite}

abstract class UnitTest extends FunSuite with DiagrammedAssertions with ScalaFutures
