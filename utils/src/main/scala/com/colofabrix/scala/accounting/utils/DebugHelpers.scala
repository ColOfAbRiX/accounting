package com.colofabrix.scala.accounting.utils

import cats.Show
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import validation._

/**
 * Helpers to print debugging values
 */
trait DebugHelpers {

  implicit def showTraversableA[A](implicit aShow: Show[A]) = new Show[Traversable[A]] {
    def show(t: Traversable[A]): String = t.map(aShow.show).mkString("", "\n", "\n")
  }

  implicit def showAValidatedA[A](implicit aShow: Show[A], listStrShow: Show[List[String]]) = new Show[AValidated[A]] {
    def show(t: validation.AValidated[A]): String = t match {
      case Valid(a)   => s"VALID\n${aShow.show(a)}\n"
      case Invalid(e) => s"INVALID\n${listStrShow.show(e.toNonEmptyList.toList)}\n"
    }
  }

  /** Prints a traversable value */
  def printT[A](input: Traversable[A])(implicit show: Show[Traversable[A]]): Unit = show.show(input)
  /** Prints a AValidated value */
  def printV[A](input: AValidated[A])(implicit show: Show[AValidated[A]]): Unit = show.show(input)
  /** Prints two traversables to compare */
  def printC[A](computed: Traversable[A], expected: Traversable[A])(implicit aShow: Show[A]): Unit = ???

}
