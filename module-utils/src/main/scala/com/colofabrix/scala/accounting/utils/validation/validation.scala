package com.colofabrix.scala.accounting.utils

import scala.util.matching.Regex
import cats.data._
import cats.implicits._
import cats.kernel.Semigroup

/**
 * Accounting Validation (AValidation) module
 */
package object validation {

  /** The type used to validate data */
  type AValidated[+A] = Validated[NonEmptyChain[String], A]

  //  ENRICHMENT CLASSES  //

  /** Enrichment for any object */
  implicit class AnyOps[A <: Any](private val anyObject: A) extends AnyVal {
    /** Mark the value as valid */
    @inline def aValid: AValidated[A] = anyObject.validNec[String]

    /** Mark the value as invalid */
    @inline def aInvalid(msg: String): AValidated[A] = msg.invalidNec[A]
  }

  /** Enrichment for String */
  implicit class ErrorContainerOps(private val stringObject: String) extends AnyVal {
    /** Mark the value as invalid */
    def aInvalid[A]: AValidated[A] = stringObject.invalidNec[A]
  }

  /** Enrichment for Either[String, A] */
  implicit class EitherOps[A](private val eitherObject: Either[String, A]) extends AnyVal {
    /** Converts an Either[String, A] into AValidated */
    def toAValidated: AValidated[A] = eitherObject.toValidatedNec
  }

  //  COMBINATORS  //

  /** Build a validation function as concatenation of the provided functions */
  def validateAll[A](
      validators: ((=> String, A) => AValidated[A])*,
  )(name: => String, value: A)(implicit aSemi: Semigroup[A]): AValidated[A] = {
    validators.foldLeft(value.aValid)(_ combine _(name, value))
  }

  //  GENERIC VALIDATORS  //

  /** Validates values to be not-null */
  def notNull[A](name: => String, value: A): AValidated[A] = {
    Option(value) match {
      case Some(v) => v.aValid
      case None    => s"'$name' cannot be null".aInvalid[A]
    }
  }

  /** Validates Iterables to be not-null and not-empty */
  def nonEmptyIterable[A <: Iterable[_]](name: => String, value: A): AValidated[A] = {
    notNull(name, value) andThen { v =>
      if (v.isEmpty) s"'$name' cannot be empty.".aInvalid[A] else v.aValid
    }
  }

  //  STRING VALIDATORS  //

  /** Validates values to be non empty strings */
  def nonEmptyString(name: => String, value: String): AValidated[String] = {
    notNull(name, value) andThen { v =>
      if (v.isEmpty) s"'$name' cannot be empty.".aInvalid[String] else v.aValid
    }
  }

  /** Validates values to match a regex */
  def matchRegex(regex: Regex, reverse: Boolean)(name: => String, value: String): AValidated[String] = {
    notNull(name, value) andThen {
      case v if v.matches(regex.regex) && !reverse => value.aValid
      case v if v.matches(regex.regex) && reverse  => s"'$name' must not match regex /${regex.regex}/".aInvalid[String]
      case _                                       => s"'$name' must match regex /${regex.regex}/".aInvalid[String]
    }
  }

  /** Validates that String contains an Int value */
  def intStringValue(name: => String, value: String): AValidated[String] = {
    matchRegex("""^\d+$""".r, reverse = false)(name, value)
  }

  /** Validates that String contains a Double value */
  def doubleStringValue(name: => String, value: String): AValidated[String] = {
    matchRegex("""[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?""".r, reverse = false)(name, value)
  }

}
