package com.colofabrix.scala.accounting.etl.pipeline

import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.logging._
import com.colofabrix.scala.accounting.utils.validation.AValidated
import com.colofabrix.scala.accounting.utils.validation.streams._
import simulacrum._

/**
 * Transforms an InputTransaction into the final Transaction
 */
@typeclass trait Normalizer[T <: InputTransaction] {
  def toTransaction(input: T): AValidated[SingleTransaction]
}

object Normalizer extends PipeLogging {
  protected[this] val logger = org.log4s.getLogger

  /**
   * Converts a given stream of InputTransaction into a stream of SingleTransaction
   */
  def apply[F[_], T <: InputTransaction](implicit n: Normalizer[T]): VPipe[F, T, SingleTransaction] = {
    val log: VPipe[F, T, T] = pipeLogger.trace(x => s"Normalizing transaction: ${x.toString}")
    val normalize: VPipe[F, T, SingleTransaction] = {
      _.map(_ andThen n.toTransaction)
    }

    log andThen normalize
  }
}
