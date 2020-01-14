package com.colofabrix.scala.accounting.etl.pipeline

import cats.effect.IO
import com.colofabrix.scala.accounting.etl._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._
import java.io.File
import kantan.csv.CsvSource

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

  import com.colofabrix.scala.accounting.utils.StreamDebugHelpers._

  /** Pipeline builder */
  def apply[T <: InputTransaction: InputProcessor: Cleaner: Normalizer]: VPipe[fs2.Pure, RawRecord, Transaction] = {
    logStream("input") andThen
    InputProcessor[T] andThen logStream("InputProcessor") andThen
    Cleaner[T] andThen logStream("Cleaner") andThen
    Normalizer[T] andThen logStream("Normalizer") andThen
    logStream("result")
  }

  /** Converts a given stream into transactions */
  def fromVStream[F[_], T <: InputTransaction: InputProcessor: Cleaner: Normalizer](
      stream: VRawInput[F],
  ): VStream[F, Transaction] = {
    stream.through(Pipeline[T])
  }

  /** Converts a given stream into transactions */
  def fromStream[F[_], T <: InputTransaction: InputProcessor: Cleaner: Normalizer](
      stream: RawInput[F],
  ): VStream[F, Transaction] = {
    fromVStream(stream.map(_.aValid))
  }

  /** Converts a given InputReader into transactions */
  def fromReader[T <: InputTransaction: InputProcessor: Cleaner: Normalizer](
      reader: InputReader,
  ): VStream[IO, Transaction] = {
    reader.read.through(Pipeline[T])
  }

  /** Converts a given File interpreted as CSV into transactions */
  def fromCsv[A: CsvSource, T <: InputTransaction: InputProcessor: Cleaner: Normalizer](
      input: A,
  ): VStream[IO, Transaction] = {
    fromReader[T](new CsvReader(input))
  }

  /** Converts a given path interpreted as CSV file into transactions */
  @deprecated("This was useful while developing")
  def fromCsvPath[T <: InputTransaction: InputProcessor: Cleaner: Normalizer](
      path: String,
  ): VStream[IO, Transaction] = {
    fromCsv[File, T](new File(path))
  }

}