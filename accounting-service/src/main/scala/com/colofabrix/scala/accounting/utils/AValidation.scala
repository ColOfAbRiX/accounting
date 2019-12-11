package com.colofabrix.scala.accounting.utils

import scala.util.Try
import scala.util.matching.Regex
import cats.data._
import cats.implicits._
import cats.kernel.{Monoid, Semigroup}


/**
  * Accounting Validation (AValidation) module
  */
object AValidation {

  /** The type used to validate Csv data */
  type AValidated[A] = ValidatedNec[String, A]

  //  ENRICHMENT CLASSES  //

  /** Enrichment for scala.util.Try */
  implicit class TryOps[A](private val tryObject: Try[A]) extends AnyVal {
    def toAValidated: AValidated[A] = {
      tryObject
        .toEither
        .leftMap(_.getMessage)
        .toValidatedNec
    }
  }

  /** Enrichment for any object */
  implicit class AnyOps[A](private val anyObject: A) extends AnyVal {
    def aValid: AValidated[A] = anyObject.validNec[String]
    def aInvalid(msg: String): AValidated[A] = msg.invalidNec[A]
  }

  /** Enrichment for String */
  implicit class ErrorContainerOps(private val stringObject: String) extends AnyVal {
    def aInvalid[A]: AValidated[A] = stringObject.invalidNec[A]
  }

  //  COMBINATORS  //

  def validateAll[A](
      validators: ((=> String, A) => AValidated[A])*)(
      name: => String, value: A)(
        implicit aSemi: Semigroup[A]): AValidated[A] = {
    validators.foldLeft(value.aValid)(_ combine _(name, value))
  }

  //  GENERIC VALIDATORS  //

  /** Validates values to be not-null */
  def notNull[A](name: => String, value: A): AValidated[A] = {
    Option(value) match {
      case Some(v) => v.aValid
      case None    => s"'$name' cannot be null".aInvalid
    }
  }

  /** Validates Iterables to be not-null and not-empty */
  def nonEmptyIterable[A <: Iterable[_]](name: => String, value: A): AValidated[A] = {
    notNull(name, value) andThen { v =>
      if (v.isEmpty) s"'$name' cannot be empty.".aInvalid else v.aValid
    }
  }

  //  STRING VALIDATORS  //

  /** Validates values to be non empty strings */
  def nonEmptyString(name: => String, value: String): AValidated[String] = {
    notNull(name, value) andThen { v =>
      if (v.isEmpty) s"'$name' cannot be empty.".aInvalid else v.aValid
    }
  }

  /** Validates values to match a regex */
  def matchRegex(regex: Regex)(name: => String, value: String, reverse: Boolean = false): AValidated[String] = {
    notNull(name, value) andThen {
      case v if v.matches(regex.regex) && !reverse => value.aValid
      case v if v.matches(regex.regex) && reverse  => s"'$name' must not match regex /${regex.regex}/".aInvalid
      case _                                       => s"'$name' must match regex /${regex.regex}/".aInvalid
    }
  }

  /** Validates that String contains an Int value */
  def intStringValue(name: => String, value: String): AValidated[String] = {
    matchRegex("""^\d+$""".r)(name, value)
  }

  /** Validates that String contains a Double value */
  def doubleStringValue(name: => String, value: String): AValidated[String] = {
    matchRegex("""[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?""".r)(name, value)
  }

}
