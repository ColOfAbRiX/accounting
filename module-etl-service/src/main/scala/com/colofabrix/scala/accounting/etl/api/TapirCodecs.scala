package com.colofabrix.scala.accounting.etl.api

import com.colofabrix.scala.accounting.etl.model.Config._
import eu.timepit.refined.types.string.NonEmptyString
import sttp.tapir.Codec._
import sttp.tapir.{ Schema, SchemaType }

/**
 * Tapir codecs to convert to and from wire values
 */
object TapirCodecs {
  /**
   * Tapir I/O codec for the type of input that can be decoded
   */
  implicit val inputTypeEndpointTapirCodec: PlainCodec[InputType] = {
    implicitly[PlainCodec[String]].map(InputType(_))(_.entryName)
  }

  /**
   * Schema for refined NonEmptyString - Tapir has issues with refined schema derivation
   */
  implicit val nonEmptyStringSchema: Schema[NonEmptyString] = Schema(SchemaType.SString)
}
