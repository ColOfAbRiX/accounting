package com.colofabrix.scala.accounting.etl.api

import cats.data.{ Validated, NonEmptyChain => NEC }
import cats.data.Validated._
import com.colofabrix.scala.accounting.etl.model.Config._
import com.colofabrix.scala.accounting.model.BankType
import io.circe._

/**
 * Circe codecs to convert to and from JSON values
 * See https://stackoverflow.com/a/59089128/1215156
 */
object CirceCodecs {

  /**
   * Encoder for InputType -> JSON
   */
  implicit val inputTypeJsonEnc: Encoder[InputType] = Encoder[String].contramap(_.entryName)

  /**
   * Encoder for BankType -> JSON
   */
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
