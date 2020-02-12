package com.colofabrix.scala.accounting.etl.api

import cats.data._
import com.colofabrix.scala.accounting.etl.model.Config._
import shapeless._

/**
 * Codecs convert to and from wire values
 */
object TapirCodecs {
  import sttp.tapir._
  import sttp.tapir.Codec._

  /** Tapir I/O codec for the type of input that can be decoded */
  implicit val inputTypeEndpointTapirCodec: PlainCodec[InputType] = {
    implicitly[PlainCodec[String]].map(InputType(_))(_.description)
  }

  //  NEWTYPE  //

  /** Tapir I/O codec for the newtype pattern */
  implicit def ntTapirCodec[NT, W](implicit gen: Generic.Aux[NT, W :: HNil], wCodec: PlainCodec[W]): PlainCodec[NT] = {
    def encode(x: NT): String               = wCodec.encode(gen.to(x).head)
    def decode(x: String): DecodeResult[NT] = wCodec.decode(x).map(w => gen.from(w :: HNil))
    implicitly[PlainCodec[String]].mapDecode(decode)(encode)
  }

}

@SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
object CirceCodecs {
  import io.circe.{ HCursor, Json }
  import io.circe.{ Decoder, Encoder }
  import cats.data.Validated._

  /**
   * Encoder for InputType -> JSON
   * See https://stackoverflow.com/a/59089128/1215156
   */
  implicit val inputTypeJsonEncoder: Encoder[InputType] = {
    Encoder[String].contramap(_.description)
  }

  /** Circe encoder for Validated */
  implicit def validatedCirceEncoder[E, A](
      implicit
      necEncoder: Encoder[NonEmptyChain[E]],
      aEncoder: Encoder[A],
  ): Encoder[Validated[NonEmptyChain[E], A]] = new Encoder[Validated[NonEmptyChain[E], A]] {
    def apply(a: Validated[NonEmptyChain[E], A]): Json = a match {
      case Invalid(e) => Json.obj(("invalid", necEncoder.apply(e)))
      case Valid(a)   => Json.obj(("valid", aEncoder.apply(a)))
    }
  }

  //  NEWTYPE  //

  /** Circe encoder for newtype case classes (case classes with only one member) */
  implicit def ntCirceEncoder[NT, W](
      implicit
      gen: Generic.Aux[NT, W :: HNil],
      wEncoder: Encoder[W],
  ): Encoder[NT] = new Encoder[NT] {
    def apply(n: NT): Json = wEncoder.apply(gen.to(n).head)
  }

  /** Circe decoder for newtype case classes (case classes with only one member) */
  implicit def ntCirceDecoder[NT, W](
      implicit
      gen: Generic.Aux[NT, W :: HNil],
      wDecoder: Decoder[W],
  ): Decoder[NT] = new Decoder[NT] {
    def apply(c: HCursor): Decoder.Result[NT] = wDecoder.apply(c).map(x => gen.from(x :: HNil))
  }

  implicit def strntCirceEncoder[NT](implicit g: Generic.Aux[NT, String :: HNil], w: Encoder[String]): Encoder[NT] =
    ntCirceEncoder[NT, String]
  implicit def strntCirceDecoder[NT](implicit g: Generic.Aux[NT, String :: HNil], d: Decoder[String]): Decoder[NT] =
    ntCirceDecoder[NT, String]

  implicit def dblntCirceEncoder[NT](implicit g: Generic.Aux[NT, Double :: HNil], w: Encoder[Double]): Encoder[NT] =
    ntCirceEncoder[NT, Double]
  implicit def dblntCirceDecoder[NT](implicit g: Generic.Aux[NT, Double :: HNil], d: Decoder[Double]): Decoder[NT] =
    ntCirceDecoder[NT, Double]

  implicit def intntCirceEncoder[NT](implicit g: Generic.Aux[NT, Int :: HNil], w: Encoder[Int]): Encoder[NT] =
    ntCirceEncoder[NT, Int]
  implicit def intntCirceDecoder[NT](implicit g: Generic.Aux[NT, Int :: HNil], d: Decoder[Int]): Decoder[NT] =
    ntCirceDecoder[NT, Int]

}
