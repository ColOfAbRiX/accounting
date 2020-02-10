package com.colofabrix.scala.accounting.etl.api

import com.colofabrix.scala.accounting.etl.model.Config._
import io.circe.{ Decoder, Encoder }
import io.circe.HCursor
import io.circe.Json
import shapeless._
import sttp.tapir.Codec._

/**
 * Codecs convert to and from wire values
 */
object InputCodecs {

  /**
   * Codec for the type of input that can be decoded
   */
  implicit val inputTypeEndpointCodec: PlainCodec[InputType] = {
    implicitly[PlainCodec[String]].map(InputType(_))(_.description)
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

  implicit def eitherEncoder[E, A](implicit ee: Encoder[E], ae: Encoder[A]): Encoder[Either[E, A]] =
    new Encoder[Either[E, A]] {
      def apply(a: Either[E, A]): Json = a match {
        case Left(value)  => Json.obj(("left", ee.apply(value)))
        case Right(value) => Json.obj(("right", ae.apply(value)))
      }
    }

  /** JSON encoder for newtype case classes (case classes with only one member) */
  implicit def newtypeEncoder[W, NT](implicit G: Generic.Aux[NT, W :: HNil], E: Encoder[W]): Encoder[NT] =
    new Encoder[NT] {
      def apply(n: NT): Json = E.apply(G.to(n).head)
    }

  /** JSON decoder for newtype case classes (case classes with only one member) */
  implicit def newtypeDecoder[W, NT](implicit G: Generic.Aux[NT, W :: HNil], D: Decoder[W]): Decoder[NT] =
    new Decoder[NT] {
      def apply(c: HCursor): Decoder.Result[NT] = D.apply(c).map(x => G.from(x :: HNil))
    }

}
