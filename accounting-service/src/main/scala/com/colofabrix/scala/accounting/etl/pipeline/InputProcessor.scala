package com.colofabrix.scala.accounting.etl.pipeline

import cats.data._
import cats.implicits._
import cats.effect._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl._
import com.colofabrix.scala.accounting.etl.inputs._
import java.io.File

/**
 * Converts an input source into transactions
 */
trait InputProcessor[T] {
  /** Converts one raw record into an input transaction */
  protected def convertRaw(record: RawRecord): AValidated[T]

  /** Filter an input to adapt it for processing, like removing head, empty rows and so on */
  protected def filterInput: VPipe[fs2.Pure, RawRecord, RawRecord]

  /** Processes the input data by filtering and converting the stream */
  def process: VPipe[fs2.Pure, RawRecord, T] = { record =>
    filterInput(record).map(_.flatMapV(convertRaw))
  }
}

object InputProcessor {
  /** Converts a given stream into transactions */
  def fromStream[T](stream: VRawInput[IO])(implicit L: InputProcessor[T]): VStream[IO, T] = {
    stream.through(L.process)
  }

  /** Converts a given InputReader into transactions */
  def fromReader[T](reader: InputReader)(implicit L: InputProcessor[T]): VStream[IO, T] = {
    reader.read.through(L.process)
  }

  /** Converts a given File interpreted as CSV into transactions */
  def fromCsvFile[T](file: File)(implicit L: InputProcessor[T]): VStream[IO, T] = {
    fromReader[T](new CsvFileReader(file))
  }

  /** Converts a given path interpreted as CSV file into transactions */
  def fromCsvPath[T](path: String)(implicit L: InputProcessor[T]): VStream[IO, T] = {
    fromCsvFile[T](new File(path))
  }

  implicit val barclaysConverter: InputProcessor[BarclaysTransaction] = InputInstances.barclaysInput
  implicit val halifaxConverter: InputProcessor[HalifaxTransaction]   = InputInstances.halifaxInput
  implicit val starlingConverter: InputProcessor[StarlingTransaction] = InputInstances.starlingInput
  implicit val amexConverter: InputProcessor[AmexTransaction]         = InputInstances.amexInput
}

/**
 * Utility functions for CsvInputProcessor
 */
object InputProcessorUtils {

  /** Represents a processor of a raw input */
  type RawInputFilter = VPipe[fs2.Pure, RawRecord, RawRecord]

  /** Drops records based on a condition on Valid data */
  private def validDropOnCondition[A](f: RawRecord => Boolean): RawInputFilter = { input =>
    input.filter(vRecord => vRecord.fold(_ => true, !f(_)))
  }

  /** Drops records based on a condition on Valid data */
  private def invalidDropOnCondition[A](f: NonEmptyChain[String] => Boolean): RawInputFilter = { input =>
    input.filter(vRecord => vRecord.fold(!f(_), _ => true))
  }

  /** Applies a function to a field that can be null */
  private def nullSafe[A](default: A, f: String => A): String => A = { field =>
    Option(field).map(f).getOrElse(default)
  }

  /** Drops the header of the input */
  def dropHeader: RawInputFilter = {
    _.drop(1)
  }

  /** Drops records that don't respect a length */
  def dropLength(check: Int => Boolean): RawInputFilter = {
    validDropOnCondition(record => check(record.length))
  }

  /** Drop a row if a match is found anywhere */
  def dropAnyMatch(p: String => Boolean): RawInputFilter = {
    validDropOnCondition { record =>
      record.exists(nullSafe(false, p))
    }
  }

  /** Drops records that are all empty or all null */
  def dropEmptyRows: RawInputFilter = {
    validDropOnCondition { record =>
      record.forall(nullSafe(true, _.trim.isEmpty))
    }
  }

  /** Converts null fields into empty strings */
  def fixNulls: RawInputFilter = { input =>
    Nested(Nested(input))
      .map(nullSafe("", identity))
      .value.value
  }

}
