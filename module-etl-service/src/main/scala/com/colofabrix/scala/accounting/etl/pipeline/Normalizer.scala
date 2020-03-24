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
  private[this] val logger = org.log4s.getLogger

  /** Converts a given stream of InputTransaction into a stream of Transaction */
  def apply[T <: InputTransaction](implicit n: Normalizer[T]): VPipe[Pure, T, Transaction] = {
    val log: VPipe[Pure, T, T] = _.debug(x => s"Normalizing input transaction ${x.toString}", logger.trace(_))
    val normalize: VPipe[Pure, T, Transaction] =
      Nested(_)
        .map(n.toTransaction)
        .value

    log andThen normalize
  }
}
