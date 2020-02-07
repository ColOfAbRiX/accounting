package com.colofabrix.scala.accounting.etl.api

import com.colofabrix.scala.accounting.etl.model.Config._
import io.circe.Encoder
import sttp.tapir.Codec._

/**
 * Codecs convert to and from wire values
 */
object InputCodecs {

  /**
   * Codec for the type of input that can be decoded
   */
  implicit val inputTypeCodec: PlainCodec[InputType] = {
    val encode: PartialFunction[String, InputType] = {
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
    implicitly[PlainCodec[String]].map(encode)(decode)
  }

}

object JsonCodecs {

  /**
   * Encoder for InputType -> JSON
   * See https://stackoverflow.com/a/59089128/1215156
   */
  implicit val inputTypeJsonEncoder: Encoder[InputType] = {
    Encoder[String].contramap(_.description)
  }

  // TODO: Make a generic encoder for newtype classes
  // import shapeless._
  // implicit def newtypeEncoder[Wrapped, Newtype](
  //     implicit
  //     G: Generic.Aux[Newtype, Wrapped :: HNil],
  //     E: Encoder[Wrapped],
  // ): Encoder[Newtype] = {
  //   Encoder[String].contramap(x: Newtype => x)
  //     // .map { wrap =>
  //     //   G.from(wrap :: HNil)
  //     // }
  //   ???
  // }

}
