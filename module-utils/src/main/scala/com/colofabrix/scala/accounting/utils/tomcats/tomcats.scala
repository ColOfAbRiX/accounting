package com.colofabrix.scala.accounting.utils

import _root_.cats._
import _root_.cats.arrow._
import _root_.cats.implicits._

package object tomcats {
  /**
   * Creates a Traversable given an isomorphism between two types
   */
  def traverseFromIso[F[_], Z[_]](forward: F ~> Z, inverse: Z ~> F)(implicit zt: Traverse[Z]): Traverse[F] =
    new Traverse[F] {
      def foldLeft[A, B](fa: F[A], b: B)(f: (B, A) => B): B =
        zt.foldLeft(forward(fa), b)(f)
      def foldRight[A, B](fa: F[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] =
        zt.foldRight(forward(fa), lb)(f)
      def traverse[G[_], A, B](fa: F[A])(f: (A) => G[B])(implicit appG: Applicative[G]): G[F[B]] = {
        zt.traverse(forward(fa))(f)(appG).map(zb => inverse(zb))
      }
    }

  /** Traverse instance for Set */
  implicit val setTraverse: Traverse[Set] = traverseFromIso(
    new FunctionK[Set, List] { def apply[X](sx: Set[X]): List[X] = sx.toList },
    new FunctionK[List, Set] { def apply[X](lx: List[X]): Set[X] = lx.toSet  },
  )

  /** Traverse instance for Seq */
  implicit val seqTraverse: Traverse[Seq] = traverseFromIso(
    new FunctionK[Seq, List] { def apply[X](sx: Seq[X]): List[X] = sx.toList },
    new FunctionK[List, Seq] { def apply[X](lx: List[X]): Seq[X] = lx        },
  )
}
