package com.colofabrix.scala.accounting.utils

import scala.util.Try
import cats.data._
import cats.kernel.Semigroup
import cats.implicits._


/**
  * Validation module
  */
object AValidation {

  /** The type used to validate Csv data */
  type AValidated[A] = ValidatedNec[Throwable, A]

  /**
    * Semigroup instance for AValidated[A]
    */
  implicit def validatedSemigroup[A](implicit aSemi: Semigroup[A]) = {
    new Semigroup[AValidated[A]] {
      def combine(x: AValidated[A], y: AValidated[A]): AValidated[A] = x combine y
    }
  }

  /**
    * Enrichment for scala.util.Try
    */
  implicit class TryOps[A](tryObject: Try[A]) {

    @inline
    def toValidatedNec: ValidatedNec[Throwable, A] = tryObject.toEither.toValidatedNec

    @inline
    def toAValidated: AValidated[A] = tryObject.toEither.toValidatedNec

  }

}
