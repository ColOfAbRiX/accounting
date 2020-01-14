package com.colofabrix.scala.accounting.utils

import cats._
import cats.data.Validated.{ Invalid, Valid }
import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.utils.validation._

/**
 * Helpers to test streams and validated streams
 */
trait StreamHelpers {
  /** Runs a function for all the elements of a stream */
  def withValidatedIoStream[O](input: VStream[IO, O])(f: List[AValidated[O]] => Unit): Unit = {
    withStream[IO, AValidated, O](input)(f).unsafeRunSync()
  }
  /** Runs a function for all the elements of a stream */
  def withStream[E[_]: Effect, F[_]: Functor, O](input: fs2.Stream[E, F[O]])(f: List[F[O]] => Unit): E[Unit] = {
    input.compile.to[List].map(f)
  }
}

/**
 * Helpers to print debugging values for streams
 */
object StreamDebugHelpers {
  def logStream[A](prefix: String): VPipe[fs2.Pure, A, A] = _.map { x =>
    println(s"$prefix=${x.toString}")
    x
  }
}

/**
 * Helpers to print debugging values
 */
@SuppressWarnings(Array("org.wartremover.warts.All"))
trait DebugHelpers {

  implicit def showIterable[A] = new Show[A] {
    def show(t: A): String = t.toString()
  }

  implicit def showIterable[A](implicit aShow: Show[A]) = new Show[Iterable[A]] {
    def show(t: Iterable[A]): String = t.map(aShow.show).mkString("", "\n", "\n")
  }

  implicit def showAValidatedA[A](implicit aShow: Show[A], listStrShow: Show[Iterable[String]]) =
    new Show[AValidated[A]] {
      def show(t: AValidated[A]): String = t match {
        case Valid(a)   => s"VALID\n${aShow.show(a)}\n"
        case Invalid(e) => s"INVALID\n${listStrShow.show(e.toNonEmptyList.toList)}\n"
      }
    }

  /** Prints an iterable value */
  def printI[A](input: Iterable[A])(implicit show: Show[Iterable[A]]): Unit = println(show.show(input))
  /** Prints a AValidated value */
  def printV[A](input: AValidated[A])(implicit show: Show[AValidated[A]]): Unit = println(show.show(input))
  /** Prints two iterables to compare */
  def printC[A](computed: Iterable[A], expected: Iterable[A]): Unit = {
    computed.zip(expected).foreach {
      case (exp, comp) =>
        println(s"Expected: ${exp.toString}")
        println(s"Computed: ${comp.toString}")
        if (exp != comp) println("DIFFERENT")
        println("")
    }
  }
}
