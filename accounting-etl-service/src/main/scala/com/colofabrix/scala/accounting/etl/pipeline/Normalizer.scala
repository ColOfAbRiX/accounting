package com.colofabrix.scala.accounting.etl.pipeline

import cats.data._
import cats.implicits._
import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._
import fs2.Pure

/**
 * Transforms an InputTransaction into the final Transaction
 */
trait Normalizer[T <: InputTransaction] {
  def toTransaction(input: T): Transaction
}

object Normalizer {
  /** Converts a given stream of InputTransaction into a stream of Transaction */
  def apply[T <: InputTransaction](implicit T: Normalizer[T]): VPipe[Pure, T, Transaction] = { input =>
    Nested(input)
      .map(T.toTransaction)
      .value
  }
}
