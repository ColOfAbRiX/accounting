package com.colofabrix.scala.accounting.etl.refined

import eu.timepit.refined.types.string.NonEmptyString
import sttp.tapir.{ Schema, SchemaType, Validator }

package object tapir {
  /**
   * Schema for refined NonEmptyString - Tapir has issues with refinement types
   */
  implicit val nonEmptyStringSchema: Schema[NonEmptyString] = Schema(SchemaType.SString)

  /**
   * Validator for refined NonEmptyString - Tapir has issues with refinement types
   */
  implicit val nonEmptyStringValidator: Validator[NonEmptyString] = Validator.pass
}
