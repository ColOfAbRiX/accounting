package com.colofabrix.scala.accounting.etl.pipeline

import cats.implicits._
import cats.data._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.inputs._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._

/**
 * Transforms an InputTransaction into the final Transaction
 */
trait Transformer[T <: InputTransaction] {
  def transform(input: T): Transaction
}

object Transformer {
  /** Converts a given stream into transactions */
  def apply[T <: InputTransaction](implicit transformer: Transformer[T]): VPipe[fs2.Pure, T, Transaction] = { input =>
    Nested(input)
      .map(transformer.transform)
      .value
  }

  implicit val barclaysTransformer: Transformer[BarclaysTransaction] =
    new Transformer[BarclaysTransaction] {
      def transform(input: BarclaysTransaction) =
        new BarclaysInputProcessor().transform(input)
    }

  implicit val halifaxTransformer: Transformer[HalifaxTransaction] =
    new Transformer[HalifaxTransaction] {
      def transform(input: HalifaxTransaction) =
        new HalifaxInputProcessor().transform(input)
    }

  implicit val starlingTransformer: Transformer[StarlingTransaction] =
    new Transformer[StarlingTransaction] {
      def transform(input: StarlingTransaction) =
        new StarlingInputProcessor().transform(input)
    }

  implicit val amexTransformer: Transformer[AmexTransaction] =
    new Transformer[AmexTransaction] {
      def transform(input: AmexTransaction) =
        new AmexInputProcessor().transform(input)
    }
}
