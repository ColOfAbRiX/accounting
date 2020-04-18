package com.colofabrix.scala.accounting.etl.conversion

import com.colofabrix.scala.accounting.utils.validation._
import eu.timepit.refined.api.Validate
import eu.timepit.refined.api.RefType
import scala.reflect.runtime.universe.WeakTypeTag

package object refined {

  implicit def refTypeFieldConverter[F[_, _], T, P](
      implicit fieldConverter: FieldConverter[String, T],
      refType: RefType[F],
      validate: Validate[T, P],
      typeTag: WeakTypeTag[F[T, P]],
  ): FieldConverter[String, F[T, P]] = new FieldConverter[String, F[T, P]] {
    def parseField(field: String): AValidated[F[T, P]] = {
      fieldConverter
        .parseField(field)
        .andThen { validated =>
          refType.refine[P](validated) match {
            case Left(e)        => s"Error converting '$field' to type ${typeTag.tpe.toString}: $e".aInvalid
            case Right(refined) => refined.aValid
          }
        }
    }
  }

}
