package com.colofabrix.scala.accounting.utils

import cats.Show
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.effect._
import validation._
import cats._
import cats.implicits._

/**
 * Helpers to print debugging values
 */
trait DebugHelpers {

  implicit def showTraversableA[A](implicit aShow: Show[A]) = new Show[Iterable[A]] {
    def show(t: Iterable[A]): String = t.map(aShow.show).mkString("", "\n", "\n")
  }

  implicit def showAValidatedA[A](implicit aShow: Show[A], listStrShow: Show[List[String]]) = new Show[AValidated[A]] {
    def show(t: validation.AValidated[A]): String = t match {
      case Valid(a)   => s"VALID\n${aShow.show(a)}\n"
      case Invalid(e) => s"INVALID\n${listStrShow.show(e.toNonEmptyList.toList)}\n"
    }
  }

  /** Prints a traversable value */
  def printT[A](input: Iterable[A])(implicit show: Show[Iterable[A]]): Unit = show.show(input)
  /** Prints a AValidated value */
  def printV[A](input: AValidated[A])(implicit show: Show[AValidated[A]]): Unit = show.show(input)
  /** Prints two traversables to compare */
  def printC[A](computed: Iterable[A], expected: Iterable[A])(implicit aShow: Show[A]): Unit = ???

  /** Runs a function for all the elements of a stream */
  def withValidatedIoStream[O](input: fs2.Stream[IO, AValidated[O]])(f: Iterable[AValidated[O]] => Unit) = {
    withStream[IO, AValidated, O](input)(f).unsafeRunSync()
  }
  /** Runs a function for all the elements of a stream */
  def withStream[E[_]: Effect, F[_]: Functor, O](input: fs2.Stream[E, F[O]])(f: Iterable[F[O]] => Unit) = {
    input.compile.to[Iterable].map(f)
  }
}
