package com.colofabrix.scala.accounting.etl.api

/**
 * Tapir codecs to convert to and from wire values
 */
object TapirCodecs {
  import com.colofabrix.scala.accounting.etl.model.Config._
  import sttp.tapir.Codec._

  /** Tapir I/O codec for the type of input that can be decoded */
  implicit val inputTypeEndpointTapirCodec: PlainCodec[InputType] = {
    implicitly[PlainCodec[String]].map(InputType(_))(_.entryName)
  }
}

/**
 * Circe codecs to convert to and from JSON values
 */
object CirceCodecs {
  import cats.data.{ Validated, NonEmptyChain => NEC }
  import cats.data.Validated.{ Invalid, Valid }
  import com.colofabrix.scala.accounting.etl.model.Config._
  import com.colofabrix.scala.accounting.model.BankType
  import io.circe.{ Encoder, Json }

  /**
   * Encoder for InputType -> JSON
   * See https://stackoverflow.com/a/59089128/1215156
   */
  implicit val inputTypeJsonEnc: Encoder[InputType] = Encoder[String].contramap(_.entryName)

  implicit val bankTypeJsonEnc: Encoder[BankType] = Encoder[String].contramap(_.entryName)

  /** Circe encoder for Validated */
  implicit def validatedEnc[E, A](implicit necEnc: Encoder[NEC[E]], aEnc: Encoder[A]): Encoder[Validated[NEC[E], A]] =
    new Encoder[Validated[NEC[E], A]] {
      def apply(a: Validated[NEC[E], A]): Json = a match {
        case Invalid(e) => Json.obj(("invalid", necEnc.apply(e)))
        case Valid(a)   => Json.obj(("valid", aEnc.apply(a)))
      }
    }
}
