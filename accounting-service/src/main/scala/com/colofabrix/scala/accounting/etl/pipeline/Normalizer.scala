package com.colofabrix.scala.accounting.etl.pipeline

import cats.data._
import cats.implicits._
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
  /** Converts a given stream of InputTransaction into a stream of Transaction */
  def apply[T <: InputTransaction](implicit T: Normalizer[T]): VPipe[fs2.Pure, T, Transaction] = { input =>
    Nested(input)
      .map(T.toTransaction)
      .value
  }

  implicit val barclaysNormalizer: Normalizer[BarclaysTransaction] = InputInstances.barclaysInput
  implicit val halifaxNormalizer: Normalizer[HalifaxTransaction]   = InputInstances.halifaxInput
  implicit val starlingNormalizer: Normalizer[StarlingTransaction] = InputInstances.starlingInput
  implicit val amexNormalizer: Normalizer[AmexTransaction]         = InputInstances.amexInput
}
