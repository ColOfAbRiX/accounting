package com.colofabrix.scala.accounting.etl.refined.shapeless

import _root_.shapeless._
import eu.timepit.refined.api.RefType

trait RefinedPoly1 extends Poly1 {
  implicit def caseRefined[F[_, _], P, T](
      implicit
      anyCase: Case.Aux[P, P],
      efType: RefType[F],
  ) = at[F[P, T]] { refined =>
    val predicate = efType.unwrap(refined)
    (predicate :: HNil).map(this)
  }
}
