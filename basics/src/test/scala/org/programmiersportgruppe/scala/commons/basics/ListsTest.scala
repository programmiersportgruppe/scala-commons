package org.programmiersportgruppe.scala.commons
package basics

class ListsTest extends UnitTest {

  import Lists._

  test("can remove one element") {
    assert(List("a", "b", "c", "b", "d") - "b" == List("a", "c", "b", "d"))
  }

  test("removing non-existent element returns original") {
    assert(List("a", "b", "c") - "d" == List("a", "b", "c"))
  }

}
