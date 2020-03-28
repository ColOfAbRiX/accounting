package com.colofabrix.scala.accounting.etl.pipeline

import cats.data._
import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.logging._
import com.colofabrix.scala.accounting.utils.validation._
import fs2.Pure

/**
 * Transforms an InputTransaction into the final Transaction
 */
trait Normalizer[T <: InputTransaction] {
  def toTransaction(input: T): Transaction
}

object Normalizer extends PipeLogging {
  protected[this] val logger = org.log4s.getLogger

  /**
   * Converts a given stream of InputTransaction into a stream of Transaction
   */
  def apply[F[_]: Sync, T <: InputTransaction](implicit n: Normalizer[T]): VPipe[Pure, T, Transaction] = {
    val log: VPipe[Pure, T, T] = pipeLogger.trace(x => s"Normalizing input transaction ${x.toString}")
    val normalize: VPipe[Pure, T, Transaction] =
      Nested(_)
        .map(n.toTransaction)
        .value

    log andThen normalize
  }
}
