package com.colofabrix.scala.accounting.etl.api

import sttp.tapir.Codec._
import com.colofabrix.scala.accounting.model._

/**
 * Codecs convert to and from wire values
 */
object Codecs {

  /**
   * Codec for the type of input that can be decoded
   */
  implicit val inputTypeCodec: PlainCodec[InputType] = {
    val code: PartialFunction[String, InputType] = {
      case "barclays" => BarclaysInputType
      case "halifax"  => HalifaxInputType
      case "starling" => StarlingInputType
      case "amex"     => AmexInputType
    }
    val decode: PartialFunction[InputType, String] = {
      case BarclaysInputType => "barclays"
      case HalifaxInputType  => "halifax"
      case StarlingInputType => "starling"
      case AmexInputType     => "amex"
    }
    implicitly[PlainCodec[String]].map(code)(decode)
  }

}
