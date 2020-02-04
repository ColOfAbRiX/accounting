package com.colofabrix.scala.accounting.etl.pipeline

import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._

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
 * The entire pipeline from the Reader on works on a fs2.Stream and each transaction
 * in the system is validated.
 * The main data structure is fs2.Stream[IO, AValidated[*]] and the pipeline has a
 * type of fs2.Pipe[IO, AValidated[*], AValidated[*]]
 */
object Pipeline {

  /** Pipeline builder */
  def apply[T <: InputTransaction: InputProcessor: Cleaner: Normalizer]: VPipe[fs2.Pure, RawRecord, Transaction] = {
    InputProcessor[T] andThen Cleaner[T] andThen Normalizer[T]
  }

}
