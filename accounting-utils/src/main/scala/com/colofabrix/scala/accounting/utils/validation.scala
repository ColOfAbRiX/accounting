package com.colofabrix.scala.accounting.utils

import scala.util.Try
import scala.util.matching.Regex
import cats.data._
import cats.data.Validated.{ Invalid, Valid }
import cats.implicits._
import cats.kernel.Semigroup
import cats.Show

/**
 * Accounting Validation (AValidation) module
 */
object validation {

  /** Representation of an error */
  type ErrorDesc = String

  /** The type used to validate data */
  type AValidated[+A] = Validated[NonEmptyChain[ErrorDesc], A]

  /** Validated Stream */
  type VStream[+F[_], +A] = fs2.Stream[F, AValidated[A]]

  /** Validated Pipe */
  type VPipe[F[_], -I, +O] = fs2.Pipe[F, AValidated[I], AValidated[O]]

  //  TYPECLASSES  //

  implicit def avalidatedShow[A](
      implicit
      se: Show[NonEmptyChain[ErrorDesc]],
      sa: Show[A],
  ): Show[AValidated[A]] = {
    new Show[AValidated[A]] {
      def show(t: AValidated[A]): String = t.fold(e => s"Invalid(${se.show(e)})", a => s"Valid(${sa.show(a)})")
    }
  }

  //  ENRICHMENT CLASSES  //

  /** Try to AValidated */
  object TryV {
    def apply[A](f: => A): AValidated[A] = Try(f).toAValidated
  }

  /** Try to Either */
  object TryE {
    def apply[A](f: => A): Either[Throwable, A] = Try(f).toEither
  }

  /** Enrichment for scala.util.Try */
  implicit class TryOps[A](private val tryObject: Try[A]) extends AnyVal {
    def toAValidated: AValidated[A] = {
      tryObject
        .toEither
        .leftMap(_.toString)
        .toValidatedNec
    }
  }

  /** Enrichment for AValidated */
  implicit class AValidatedOps[A](private val avObject: AValidated[A]) extends AnyVal {
    def flatMapV[B](f: A => AValidated[B]): AValidated[B] = avObject match {
      case i @ Invalid(_) => i
      case Valid(a)       => f(a)
    }
  }

  /** Enrichment for any object */
  implicit class AnyOps[A <: Any](private val anyObject: A) extends AnyVal {
    /** Mark the value as valid */
    def aValid: AValidated[A] = anyObject.validNec[ErrorDesc]

    /** Mark the value as invalid */
    def aInvalid(msg: ErrorDesc): AValidated[A] = msg.invalidNec[A]
  }

  /** Enrichment for String */
  implicit class ErrorContainerOps(private val stringObject: ErrorDesc) extends AnyVal {
    /** Mark the value as invalid */
    def aInvalid[A]: AValidated[A] = stringObject.invalidNec[A]
  }

  /** Enrichment for Either[String, A] */
  implicit class EitherOps[A](private val eitherObject: Either[ErrorDesc, A]) extends AnyVal {
    /** Converts an Either[String, A] into AValidated */
    def toAValidated: AValidated[A] = eitherObject.toValidatedNec
  }

  /** Enrichment for Either[NonEmptyChain[String], A] */
  implicit class EitherNecOps[A](private val eitherObject: Either[NonEmptyChain[ErrorDesc], A]) extends AnyVal {
    /** Converts an Either[NonEmptyChain[String], A] into AValidated */
    def toAValidated: AValidated[A] = eitherObject match {
      case Left(error)  => Validated.invalid[NonEmptyChain[ErrorDesc], A](error)
      case Right(value) => value.aValid
    }
  }

  //  COMBINATORS  //

  // Validator/Vtor
  private type Vtor[A] = ((=> String, A) => AValidated[A])

  /** Build a validation function as concatenation of the provided functions */
  def validateAll[A](validators: Vtor[A]*)(name: => String, value: A)(implicit aSemi: Semigroup[A]): AValidated[A] = {
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
    matchRegex("""^\d+$""".r, false)(name, value)
  }

  /** Validates that String contains a Double value */
  def doubleStringValue(name: => String, value: String): AValidated[String] = {
    matchRegex("""[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?""".r, false)(name, value)
  }

}