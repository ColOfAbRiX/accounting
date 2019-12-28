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

  //  UTILITIES

  /** Drops the header of the input */
  def dropHeader(input: VRawInput[fs2.Pure]): VRawInput[fs2.Pure] = input.drop(1)

  /** Drop a row if a match is found anywhere */
  def dropAnyMatch(p: String => Boolean)(input: VRawInput[fs2.Pure]): VRawInput[fs2.Pure] = {
    input.filter(vRecord => vRecord.fold(_ => true, _.exists(p)))
  }

  /** Drops the empty records */
  def dropEmpty(input: VRawInput[fs2.Pure]): VRawInput[fs2.Pure] = {
    def check(nullable: String): Boolean = Option(nullable).map(_.trim.nonEmpty).getOrElse(false)
    input.filter { vRecord =>
      vRecord.fold(_ => true, _.filter(check).nonEmpty)
    }
  }
}
