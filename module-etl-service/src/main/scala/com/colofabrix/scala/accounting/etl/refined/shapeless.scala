package com.colofabrix.scala.accounting.etl.refined

import cats._
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

    /** Mapping case for Refined[T, P] => AValidated[Refined[T, P]] */
    implicit def caseRefinedTP[T, P](
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

    /** Mapping case for F[A] => AValidated[F[A]] */
    implicit def caseFA[F[_]: Traverse, A](implicit baseCase: ValidatedCase[A]): ValidatedCase[F[A]] = at[F[A]] {
      Traverse[F].traverse(_)(baseCase.apply(_))
    }
  }

}
