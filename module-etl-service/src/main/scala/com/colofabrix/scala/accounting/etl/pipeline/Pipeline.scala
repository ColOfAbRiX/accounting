package com.colofabrix.scala.accounting.etl.pipeline

import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.logging._
import com.colofabrix.scala.accounting.utils.validation.streams._
import cats.effect.Sync

/**
 * The ETL pipeline
 *
 * [Input] -> Reader -> InputProcessor -> Cleaner -> Normalizer -> [Output]
 *         ^         ^                 ^          ^             ^
 *        /          |                 |          |             |
 *    Various    RawRecord             |   +------+        Transaction
 *                                     |  /
 *                              InputTransaction
 *
 * NOTES
 * The entire pipeline from the Reader on works on a Stream and each transaction
 * in the system is validated.
 * The main data structure is Stream[F, AValidated[*]] and the pipeline has a
 * type of Pipe[IO, AValidated[*], AValidated[*]]
 */
object Pipeline extends PipeLogging {
  protected[this] val logger = org.log4s.getLogger

  /** Pipeline builder */
  def apply[F[_]: Sync, T <: InputTransaction: InputProcessor: Cleaner: Normalizer]
      : VPipe[F, RawRecord, SingleTransaction] = {

    val inputLog: VPipe[F, RawRecord, RawRecord] =
      pipeLogger.debug("Running ETL pipeline") andThen
      pipeLogger.trace(x => s"ETL pipeline working on: ${x.toString}")

    val outputLog: VPipe[F, SingleTransaction, SingleTransaction] =
      pipeLogger.trace(x => s"ETL pipeline result: ${x.toString}")

    inputLog andThen InputProcessor[F, T] andThen Cleaner[F, T] andThen Normalizer[F, T] andThen outputLog
  }
}
