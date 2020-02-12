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

  /** Tapir I/O codec for the newtype pattern */
  implicit def newtypeTapirCodec[W, NT](
      implicit
      G: Generic.Aux[NT, W :: HNil],
      wCodec: PlainCodec[W],
  ): PlainCodec[NT] = {
    def encode(x: NT): String               = wCodec.encode(G.to(x).head)
    def decode(x: String): DecodeResult[NT] = wCodec.decode(x).map(w => G.from(w :: HNil))
    implicitly[PlainCodec[String]].mapDecode(decode)(encode)
  }

  //  From sttp.tapir.codec.cats.TapirCodecCats.scala  //

  private def nonEmptyValidator[T]: Validator[List[T]] = Validator.minSize[T, List](1)

  implicit def validatorNec[T](implicit v: Validator[T]): Validator[NonEmptyChain[T]] = {
    v.asIterableElements
      .and(nonEmptyValidator[T])
      .contramap(_.toChain.toList)
  }

  implicit def schemaForNec[T: Schema]: Schema[NonEmptyChain[T]] = {
    Schema[NonEmptyChain[T]](
      SchemaType.SArray(implicitly[Schema[T]]),
    ).copy(isOptional = false)
  }

}

object CirceCodecs {
  import io.circe.{ HCursor, Json }
  import io.circe.{ Decoder, Encoder }
  import cats.data.Validated._
  import com.colofabrix.scala.accounting.utils.validation.{ ValidationError => VError }

  /**
   * Encoder for InputType -> JSON
   * See https://stackoverflow.com/a/59089128/1215156
   */
  implicit val inputTypeJsonEncoder: Encoder[InputType] = {
    Encoder[String].contramap(_.description)
  }

  /** Circe encoder for Validated */
  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def validatedEncoder[A](
      implicit
      necEncoder: Encoder[NonEmptyChain[VError]],
      aEncoder: Encoder[A],
  ): Encoder[Validated[NonEmptyChain[VError], A]] = new Encoder[Validated[NonEmptyChain[VError], A]] {
    def apply(a: Validated[NonEmptyChain[VError], A]): Json = a match {
      case Invalid(e) => Json.obj(("invalid", necEncoder.apply(e)))
      case Valid(a)   => Json.obj(("valid", aEncoder.apply(a)))
    }
  }

  /** Circe encoder for newtype case classes (case classes with only one member) */
  implicit def newtypeCirceEncoder[W, NT](implicit G: Generic.Aux[NT, W :: HNil], E: Encoder[W]): Encoder[NT] =
    new Encoder[NT] {
      def apply(n: NT): Json = E.apply(G.to(n).head)
    }

  /** Circe decoder for newtype case classes (case classes with only one member) */
  implicit def newtypeCirceDecoder[W, NT](implicit G: Generic.Aux[NT, W :: HNil], D: Decoder[W]): Decoder[NT] =
    new Decoder[NT] {
      def apply(c: HCursor): Decoder.Result[NT] = D.apply(c).map(x => G.from(x :: HNil))
    }

}
