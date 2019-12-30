package com.colofabrix.scala.accounting.etl.csv

import cats.implicits._
import com.colofabrix.scala.accounting.etl._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._
import cats.effect.IO
import fs2.Pipe
import cats.data.Validated.Invalid
import cats.data.Validated.Valid

/**
 * Processes a CSV file like filtering bad records and converting them to case classes
 */
trait CsvProcessor[+T <: InputTransaction] {
  /** Converts one CSV record into an input transaction */
  protected def convert(record: RawRecord): AValidated[T]

  /** Filter an input to adapt it for processing, like removing head, empty rows and so on */
  protected def filter(input: VRawInput[fs2.Pure]): VRawInput[fs2.Pure]

  /** Processes the input data by filtering and converting the stream */
  def process: VPipe[fs2.Pure, RawRecord, T] = { record =>
    filter(record).map(_.flatMapV(convert))
  }

  //  UTILITY FUNCTIONS

  def filterValid(f: RawRecord => Boolean)(input: VRawInput[fs2.Pure]): VRawInput[fs2.Pure] = {
    input.filter(vRecord => vRecord.fold(_ => true, f))
  }

  /** Drops the header of the input */
  def dropHeader(input: VRawInput[fs2.Pure]): VRawInput[fs2.Pure] = input.drop(1)

  /** Drops records that don't respect a length */
  def dropLength(length: Int)(input: VRawInput[fs2.Pure]): VRawInput[fs2.Pure] = {
    filterValid(_.length != length)(input)
  }

  /** Drop a row if a match is found anywhere */
  def dropAnyMatch(p: String => Boolean)(input: VRawInput[fs2.Pure]): VRawInput[fs2.Pure] = {
    filterValid(_.exists(p))(input)
  }

  /** Drops records that are all empty or all null */
  def dropEmptyRows(input: VRawInput[fs2.Pure]): VRawInput[fs2.Pure] = {
    def check(field: String): Boolean = Option(field).map(_.trim.nonEmpty).getOrElse(false)
    filterValid(_.filter(check).nonEmpty)(input)
  }

  /** Converts null fields into empty strings */
  def fixNulls(input: VRawInput[fs2.Pure]): VRawInput[fs2.Pure] = {
    input.map { vRecord =>
      vRecord.map(_.map(Option(_).getOrElse("")))
    }
  }
}
