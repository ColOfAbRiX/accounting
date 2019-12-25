package com.colofabrix.scala.accounting.etl.csv

import cats._
import cats.implicits._
import java.io.File
import com.colofabrix.scala.accounting.etl._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._
import cats.data.Validated.Valid
import cats.data.Validated.Invalid
import cats.data.Nested

/**
 * Processes a CSV file like filtering bad rows and converting them to case classes
 */
trait CsvProcessor[+T <: InputTransaction] {
  /** Filter a file to adapt it for processing */
  def filterFile(file: RawInput): AValidated[RawInput]

  /** Converts a Csv record */
  def convertRecord(record: RawRecord): AValidated[T]

  //  UTILITIES

  /** Drops the header of the input */
  def dropHeader(input: RawInput): RawInput = input.drop(1)
  /** Drops the empty records */
  def dropEmpty(input: RawInput): RawInput = {
    input.filter {
      _.filter {
        Option(_)
          .map(_.trim.nonEmpty)
          .getOrElse(false)
      }.nonEmpty
    }
  }
}

/**
 * Converts a CSV input into transactions
 */
class CsvInputConverter[+T <: InputTransaction](reader: CsvReader, processor: CsvProcessor[T])
    extends InputConverter[T] {

  /** Processes the entire content provided by the Input Reader */
  def ingestInput: AValidated[List[T]] = {
    reader.read.flatMapV {
      processor.filterFile(_).flatMapV {
        _.traverse(record => processor.convertRecord(record))
      }
    }
  }

}
