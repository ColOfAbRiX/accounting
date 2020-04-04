package com.colofabrix.scala.accounting.utils

import cats._
import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.utils.validation._
import com.colofabrix.scala.accounting.utils.validation.streams._

/**
 * Helpers to test streams and validated streams
 */
trait StreamHelpers {
  /** Runs a function for all the elements of a stream */
  def withValidatedIoStream[O](input: VStream[IO, O])(f: List[AValidated[O]] => Unit): Unit = {
    withValidatedStream[IO, O](input)(f).unsafeRunSync()
  }

  /** Runs a function for all the elements of a stream */
  def withValidatedStream[F[_]: Sync, O](input: VStream[F, O])(f: List[AValidated[O]] => Unit): F[Unit] = {
    withStream[F, AValidated, O](input)(f)
  }

  /** Runs a function for all the elements of a stream */
  def withStream[F[_]: Sync, E[_], O](input: fs2.Stream[F, E[O]])(f: List[E[O]] => Unit): F[Unit] = {
    input.compile.toList.map(f)
  }
}

/**
 * Helpers to print debugging values
 */
trait DebugHelpers {

  implicit def showIterable[A]: Show[A] = new Show[A] {
    def show(t: A): String = t.toString()
  }

  implicit def showIterable[A](implicit aShow: Show[A]): Show[Iterable[A]] = new Show[Iterable[A]] {
    def show(t: Iterable[A]): String = t.map(aShow.show).mkString("", "\n", "\n")
  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def showAValidatedA[A](implicit aShow: Show[A], listStrShow: Show[Iterable[String]]): Show[AValidated[A]] =
    new Show[AValidated[A]] {
      def show(t: AValidated[A]): String = t.fold(
        e => s"INVALID\n${listStrShow.show(e.toNonEmptyList.toList)}\n",
        a => s"VALID\n${aShow.show(a)}\n",
      )
    }

  /** Prints an iterable value */
  def printI[A](input: Iterable[A])(implicit show: Show[Iterable[A]]): Unit = println(show.show(input))
  /** Prints a AValidated value */
  def printV[A](input: AValidated[A])(implicit show: Show[AValidated[A]]): Unit = println(show.show(input))
  /** Prints two iterables to compare */
  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
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
