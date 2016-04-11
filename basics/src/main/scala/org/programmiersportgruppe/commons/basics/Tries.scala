package org.programmiersportgruppe.commons.basics

import scala.util.{Failure, Try}

object Tries {

    /** Extension methods for Try */
    implicit class AugmentedTry[T](val self: Try[T]) extends AnyVal {

        /** Execute action if the Try is a Failure.
          *
          * Can be used to cause a side effect in case something went wrong,
          * e.g. for logging before doing a Try.toOption.
          */
        def onFailure(action: Throwable => Unit): Try[T] = {
            self match {
                case Failure(t) => action(t)
                case _ =>
            }
            self
        }
    }

}
