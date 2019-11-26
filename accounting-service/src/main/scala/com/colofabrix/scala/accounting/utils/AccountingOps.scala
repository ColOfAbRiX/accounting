package com.colofabrix.scala.accounting.utils

import scala.util.Try
import cats.data._
import cats.implicits._


object AccountingOps {

  /**
    * Enrichment for scala.util.Try
    */
  implicit class TryOps[A](tryObject: Try[A]) {

    def toValidated: Validated[Throwable, A] = tryObject.toEither.toValidated
    def toValidatedNel: ValidatedNel[Throwable, A] = tryObject.toEither.toValidatedNel
    def toValidatedNec: ValidatedNec[Throwable, A] = tryObject.toEither.toValidatedNec

  }

}
