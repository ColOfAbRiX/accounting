package com.colofabrix.scala.accounting.etl.pipeline

import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._
import com.colofabrix.scala.accounting.utils.PipeLogging
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
object Pipeline {

  /** Pipeline builder */
  def apply[F[_]: Sync, T <: InputTransaction: InputProcessor: Cleaner: Normalizer]
      : VPipe[F, RawRecord, Transaction] = {

    val inputLog: VPipe[F, RawRecord, RawRecord] =
      PipeLogging.trace(x => s"Applying pipeline to record ${x.toString}")

    val outputLog: VPipe[F, Transaction, Transaction] =
      PipeLogging.trace(x => s"Resulting transaction from pipeline ${x.toString}")

    inputLog andThen InputProcessor[F, T] andThen Cleaner[F, T] andThen Normalizer[F, T] andThen outputLog
  }

}