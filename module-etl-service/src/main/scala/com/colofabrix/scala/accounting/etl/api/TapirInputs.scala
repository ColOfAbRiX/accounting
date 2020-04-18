package com.colofabrix.scala.accounting.etl.api

import com.colofabrix.scala.accounting.etl.model.Config._
import sttp.tapir.Codec.PlainCodec
import sttp.tapir._

/**
 * Inputs are the values that can be accepted by an endpoint
 */
object TapirInputs {
  /**
   * Tapir I/O codec for the type of input that can be decoded
   */
  implicit val inputTypeEndpointTapirCodec: PlainCodec[InputType] = {
    implicitly[PlainCodec[String]].map(InputType(_))(_.entryName)
  }

  /**
   * An input representing the type of record
   */
  val inputTypeQuery: EndpointInput[InputType] =
    query[InputType]("inputType")
      .description("The type of input to convert")
      .example(InputType.BarclaysInputType)
}
