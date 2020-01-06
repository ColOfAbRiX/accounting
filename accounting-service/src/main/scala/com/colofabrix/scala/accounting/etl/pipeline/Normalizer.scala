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
  /** Converts a given stream of InputTransaction into a stream of Transaction */
  def apply[T <: InputTransaction](implicit T: Normalizer[T]): VPipe[fs2.Pure, T, Transaction] = { input =>
    Nested(input)
      .map(T.toTransaction)
      .value
  }

  implicit val barclaysTransformer: Normalizer[BarclaysTransaction] = InputInstances.barclaysInput
  implicit val halifaxTransformer: Normalizer[HalifaxTransaction]   = InputInstances.halifaxInput
  implicit val starlingTransformer: Normalizer[StarlingTransaction] = InputInstances.starlingInput
  implicit val amexTransformer: Normalizer[AmexTransaction]         = InputInstances.amexInput
}
