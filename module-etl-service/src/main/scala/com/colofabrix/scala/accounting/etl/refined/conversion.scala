package com.colofabrix.scala.accounting.etl.refined

import com.colofabrix.scala.accounting.etl.conversion._
import com.colofabrix.scala.accounting.utils.validation._
import eu.timepit.refined.api.{ RefType, Refined, Validate }
import scala.reflect.runtime.universe.WeakTypeTag

package object conversion {

  /**
   * Refinement conversion for FieldConverter
   */
  implicit def refTypeFieldConverter[T, P](
      implicit fieldConverter: FieldConverter[T],
      refType: RefType[Refined],
      validate: Validate[T, P],
      typeTag: WeakTypeTag[Refined[T, P]],
  ): FieldConverter[Refined[T, P]] = new FieldConverter[Refined[T, P]] {
    def parseField(field: String): AValidated[Refined[T, P]] = {
      fieldConverter
        .parseField(field)
        .andThen { validated =>
          refType.refine[P](validated) match {
            case Left(error)    => s"Error converting '$field' to type ${typeTag.tpe.toString}: $error".aInvalid
            case Right(refined) => refined.aValid
          }
        }
    }
  }

}
