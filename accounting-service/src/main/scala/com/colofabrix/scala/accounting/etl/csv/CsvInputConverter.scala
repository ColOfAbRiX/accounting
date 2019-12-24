package com.colofabrix.scala.accounting.etl.csv

import cats._
import cats.implicits._
import java.io.File
import com.colofabrix.scala.accounting.etl._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._

/**
 * Processes a CSV file like filtering bad rows and converting them to case classes
 */
trait CsvProcessor[+T <: InputTransaction] {
  /** Converts a Csv row into a BankTransaction */
  def filterFile(file: RawInput): RawInput

  /** Converts a Csv row */
  def convertRow(row: RawRecord): AValidated[T]

  //  UTILITIES

  /** Drops the header of the input */
  def dropHeader(input: RawInput): RawInput = input.drop(1)
  /** Drops the empty records */
  def dropEmpty(input: RawInput): RawInput = {
    input.foreach(println)
    input.filter(_.filter(_.trim.nonEmpty).nonEmpty)
  }
}

/**
 * Converts a CSV input into transactions
 */
class CsvInputConverter[+T <: InputTransaction](reader: CsvReader, processor: CsvProcessor[T])
    extends InputConverter[T] {

  /** Processes the entire content provided by the Input Reader */
  def ingestInput: AValidated[List[T]] = {
    for {
      rawInput     <- reader.read
      filtered     <- processor.filterFile(rawInput).aValid
      transactions <- filtered.map(processor.convertRow).sequence
    } yield {
      transactions
    }
  }

}
