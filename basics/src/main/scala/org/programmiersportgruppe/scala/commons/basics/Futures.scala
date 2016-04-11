package org.programmiersportgruppe.scala.commons.basics

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Try
import scala.util.control.NonFatal


object Futures {

  implicit class AugmentedFuture[A](val self: Future[A]) extends AnyVal {

    /** Map over the inner `Try[A]` completion value of the future,
      * rather than the `A` success value like the normal [[scala.concurrent.Future.map]] method.
      */
    final def mapTry[B](f: Try[A] => B)(implicit executor: ExecutionContext): Future[B] = {
      val p = Promise[B]()
      self.onComplete(result => p.complete(Try(f(result))))
      p.future
    }

    /** Map over the inner `Try[A]` completion value of the future,
      * rather than the `A` success value like the normal [[scala.concurrent.Future.flatMap]] method.
      */
    final def flatMapTry[B](fn: Try[A] => Future[B])(implicit executor: ExecutionContext): Future[B] = {
      val p = Promise[B]()
      self.onComplete(result => p.completeWith(
        try fn(result)
        catch { case NonFatal(e) => Future.failed(e) }
      ))
      p.future
    }

    /** Converts the "inner Try" of the Future to an option,
      * wrapping successful values in Some and replacing failures with None.
      *
      * This is useful if the result will just not be used in the case of an
      * error.
      */
    def toOption(implicit executor: ExecutionContext): Future[Option[A]] = {
      val p = Promise[Option[A]]()
      self.onComplete(result => p.success(result.toOption))
      p.future
    }


    /** Causes an asynchronous side effect in case the future fails.
      *
      * Note that the original future is returned,
      * meaning that it will be completed before the action has completed.
      */
    def handleFailure(action: Throwable => Unit)(implicit executor: ExecutionContext): Future[A] = {
      self.onFailure { case t => action(t) }
      self
    }

  }

}
