package com.colofabrix.scala.accounting.etl.api

import com.colofabrix.scala.accounting.etl.model.Config._
import sttp.tapir.Codec._

/**
 * Codecs convert to and from wire values
 */
object Codecs {

  /**
   * Codec for the type of input that can be decoded
   */
  implicit val inputTypeCodec: PlainCodec[InputType] = {
    val code: PartialFunction[String, InputType] = {
      case BarclaysInputType.description => BarclaysInputType
      case HalifaxInputType.description  => HalifaxInputType
      case StarlingInputType.description => StarlingInputType
      case AmexInputType.description     => AmexInputType
    }
    val decode: PartialFunction[InputType, String] = {
      case BarclaysInputType => BarclaysInputType.description
      case HalifaxInputType  => HalifaxInputType.description
      case StarlingInputType => StarlingInputType.description
      case AmexInputType     => AmexInputType.description
    }
    implicitly[PlainCodec[String]].map(code)(decode)
  }

}
