package com.colofabrix.scala.accounting.etl.pipeline

import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._
import fs2.Pure

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
 * The main data structure is Stream[IO, AValidated[*]] and the pipeline has a
 * type of Pipe[IO, AValidated[*], AValidated[*]]
 */
object Pipeline {

  /** Pipeline builder */
  def apply[T <: InputTransaction: InputProcessor: Cleaner: Normalizer]: VPipe[Pure, RawRecord, Transaction] = {
    InputProcessor[T] andThen Cleaner[T] andThen Normalizer[T]
  }

}
