package org.programmiersportgruppe.commons.basics

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Failure


object Futures {

    implicit class AugmentedFuture[T](val self: Future[T]) extends AnyVal {

        /** Converts the "inner Try" of the Future to an option, discarding the
          * Throw.
          *
          * This is useful if the result will just not be used in the case of an
          * error.
          */
        def toOption(implicit executor: ExecutionContext): Future[Option[T]] = {
            //            self
            //                .map(Some(_))
            //                .recover { case t: Throwable => None }
            // Scala 2.11 is happy, 2.10 is not; I think it's a bug in 2.10 that we would have to work around

            // Here's the more efficient Promise implementation
            val p = Promise[Option[T]]()
            self.onComplete(result => p.success(result.toOption))
            p.future
        }


        /** Causes a side effect in case the future fails. */
        def  handleFailure(action: Throwable => Unit)(implicit executor: ExecutionContext): Future[T] = {
            self.onFailure { case t => action(t) }
            self

            // Is this method useful? it's equivalent to
            // self.andThen { case Failure(t) => action(t) }
        }

    }

}
