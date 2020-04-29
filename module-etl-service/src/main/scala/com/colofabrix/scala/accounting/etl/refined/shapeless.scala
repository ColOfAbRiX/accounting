package com.colofabrix.scala.accounting.etl.refined

import _root_.shapeless._
import com.colofabrix.scala.accounting.utils.validation._
import eu.timepit.refined.api.{ RefType, Refined, Validate }

object shapeless {

  /**
   * Shapeless Poly1 with support for Refined types
   */
  trait RefinedPoly1 extends Poly1 {
    type ValidatedCase[A]  = Case.Aux[A, AValidated[A]]
    type RefinedCase[T, P] = ValidatedCase[Refined[T, P]]

    implicit def caseRefined[T, P](
        implicit
        baseCase: ValidatedCase[T],
        refType: RefType[Refined],
        validate: Validate[T, P],
    ): RefinedCase[T, P] = at[Refined[T, P]] { refined =>
      val predicate  = refType.unwrap(refined)
      val baseMapped = baseCase.apply(predicate :: HNil)
      baseMapped andThen { value =>
        refType
          .refine[P](value).fold(
            error => s"Error mapping value '${predicate.toString}'}: $error".aInvalid,
            refined => refined.aValid,
          )
      }
    }
  }

}
