package com.colofabrix.scala.accounting.etl.api

import com.colofabrix.scala.accounting.etl.model.Config._
import sttp.tapir.Codec._

/**
 * Tapir codecs to convert to and from wire values
 */
object TapirCodecs {

  /** Tapir I/O codec for the type of input that can be decoded */
  implicit val inputTypeEndpointTapirCodec: PlainCodec[InputType] = {
    implicitly[PlainCodec[String]].map(InputType(_))(_.entryName)
  }
}
