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
trait Normalizer[T <: InputTransaction] {
  def toTransaction(input: T): Transaction
}

object Normalizer {
  /** Converts a given stream into transactions */
  def apply[T <: InputTransaction](implicit T: Normalizer[T]): VPipe[fs2.Pure, T, Transaction] = { input =>
    Nested(input)
      .map(T.toTransaction)
      .value
  }

  implicit val barclaysTransformer: Normalizer[BarclaysTransaction] =
    new Normalizer[BarclaysTransaction] {
      def toTransaction(input: BarclaysTransaction) =
        InputInstances.barclaysInput.toTransaction(input)
    }

  implicit val halifaxTransformer: Normalizer[HalifaxTransaction] =
    new Normalizer[HalifaxTransaction] {
      def toTransaction(input: HalifaxTransaction) =
        InputInstances.halifaxInput.toTransaction(input)
    }

  implicit val starlingTransformer: Normalizer[StarlingTransaction] =
    new Normalizer[StarlingTransaction] {
      def toTransaction(input: StarlingTransaction) =
        InputInstances.starlingInput.toTransaction(input)
    }

  implicit val amexTransformer: Normalizer[AmexTransaction] =
    new Normalizer[AmexTransaction] {
      def toTransaction(input: AmexTransaction) =
        InputInstances.amexInput.toTransaction(input)
    }
}
