package com.colofabrix.scala.accounting.etl.refined.shapeless

import _root_.shapeless._
import eu.timepit.refined.api.{ RefType, Refined }

trait RefinedPoly1 extends Poly1 {
  type RefinedCase[P, T] = Case[Refined[P, T]] { type Result = Refined[P, T] }

  implicit def caseRefined[P, T](implicit baseCase: Case.Aux[P, P], efType: RefType[Refined]): RefinedCase[P, T] =
    at[Refined[P, T]] { refined =>
      val predicate = efType.unwrap(refined)
      val a: P      = baseCase.apply(predicate :: HNil)
      Refined.unsafeApply[P, T](a)
    }
}
