package org.programmiersportgruppe.scala.commons.basics

import scala.concurrent.{ExecutionContext, Future, Promise}


object Futures {

  implicit class AugmentedFuture[T](val self: Future[T]) extends AnyVal {

    /** Converts the "inner Try" of the Future to an option,
      * wrapping successful values in Some and replacing failures with None.
      *
      * This is useful if the result will just not be used in the case of an
      * error.
      */
    def toOption(implicit executor: ExecutionContext): Future[Option[T]] = {
      val p = Promise[Option[T]]()
      self.onComplete(result => p.success(result.toOption))
      p.future
    }


    /** Causes an asynchronous side effect in case the future fails.
      *
      * Note that the original future is returned,
      * meaning that it will be completed before the action has completed.
      */
    def handleFailure(action: Throwable => Unit)(implicit executor: ExecutionContext): Future[T] = {
      self.onFailure { case t => action(t) }
      self
    }

  }

}
