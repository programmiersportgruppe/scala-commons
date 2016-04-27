package org.programmiersportgruppe.scala.commons
package basics

object Lists {

  implicit final class AugmentedList[A](val self: List[A]) extends AnyVal {

    /** Removes the first occurrence of a value from the list.
      *
      * This method uses a stack frame per element until it finds a matching value or the end of the list,
      * so it's not great for large lists, but is good for short lists.
      */
    def -(value: A): List[A] =
      self match {
        case Nil => Nil
        case head :: tail =>
          if (head == value) tail
          else head :: new AugmentedList(tail) - value
      }

  }

}
