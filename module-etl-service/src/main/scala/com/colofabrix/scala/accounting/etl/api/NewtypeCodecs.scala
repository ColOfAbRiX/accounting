package com.colofabrix.scala.accounting.etl.api

import shapeless._

/**
 * Tapir I/O codec for the newtype pattern
 * */
object TapirNewtypeCodecs {
  import sttp.tapir._
  import sttp.tapir.Codec._

  private type GenNewt[NT, W] = Generic.Aux[NT, W :: HNil]

  implicit def newtCodec[NT, W](implicit gen: GenNewt[NT, W], wCodec: PlainCodec[W]): PlainCodec[NT] = {
    def encode(x: NT): String               = wCodec.encode(gen.to(x).head)
    def decode(x: String): DecodeResult[NT] = wCodec.decode(x).map(w => gen.from(w :: HNil))
    implicitly[PlainCodec[String]].mapDecode(decode)(encode)
  }
}

/**
 * Circe codecs for the newtype pattern
 */
@SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
object CirceNewtypeCodecs {
  import io.circe.{ HCursor, Json, Decoder => Dec, Encoder => Enc }

  private type GenNewt[NT, W] = Generic.Aux[NT, W :: HNil]

  /** Circe encoder for newtype case classes (case classes with only one member) */
  private[this] def newtEnc[NT, W](implicit gen: GenNewt[NT, W], wEnc: Enc[W]): Enc[NT] = new Enc[NT] {
    def apply(n: NT): Json = wEnc.apply(gen.to(n).head)
  }

  /** Circe decoder for newtype case classes (case classes with only one member) */
  private[this] def newtDec[NT, W](implicit gen: GenNewt[NT, W], wDec: Dec[W]): Dec[NT] = new Dec[NT] {
    def apply(c: HCursor): Dec.Result[NT] = wDec.apply(c).map(x => gen.from(x :: HNil))
  }

  implicit def strNewtEnc[NT](implicit g: GenNewt[NT, String], w: Enc[String]): Enc[NT] = newtEnc[NT, String]
  implicit def strNewtDec[NT](implicit g: GenNewt[NT, String], d: Dec[String]): Dec[NT] = newtDec[NT, String]

  implicit def dblNewtEnc[NT](implicit g: GenNewt[NT, Double], w: Enc[Double]): Enc[NT] = newtEnc[NT, Double]
  implicit def dblNewtDec[NT](implicit g: GenNewt[NT, Double], d: Dec[Double]): Dec[NT] = newtDec[NT, Double]

  implicit def intNewtEnc[NT](implicit g: GenNewt[NT, Int], w: Enc[Int]): Enc[NT] = newtEnc[NT, Int]
  implicit def intNewtDec[NT](implicit g: GenNewt[NT, Int], d: Dec[Int]): Dec[NT] = newtDec[NT, Int]

  implicit def boolNewtEnc[NT](implicit g: GenNewt[NT, Boolean], w: Enc[Boolean]): Enc[NT] = newtEnc[NT, Boolean]
  implicit def boolNewtDec[NT](implicit g: GenNewt[NT, Boolean], d: Dec[Boolean]): Dec[NT] = newtDec[NT, Boolean]
}
