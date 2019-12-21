package com.colofabrix.scala.accounting.etl.csv

import cats._
import cats.implicits._
import java.io.File
import com.colofabrix.scala.accounting.etl._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._
import com.colofabrix.scala.accounting.etl.definitions._
import shapeless._
import cats.data.Validated.Invalid
import cats.data.Validated.Valid

/**
 * Processes a CSV file like filtering bad rows and converting them to case classes
 */
trait CsvProcessor[T <: InputTransaction] {
  /** Converts a Csv row into a BankTransaction */
  def filterFile(file: RawInput): RawInput

  /** Converts a Csv row */
  def convertRow(row: RawRecord): AValidated[T]
}

/**
 * Converts a CSV input into transactions
 */
class CsvInputConverter[T <: InputTransaction](reader: CsvReader)(implicit processor: CsvProcessor[T])
    extends InputConverter[T] {

  /** Processes the entire content provided by the Input Reader */
  def ingestInput: AValidated[List[T]] = {
    reader.read match {
      case i @ Invalid(_) => i
      case Valid(rawInput) =>
        processor
          .filterFile(rawInput)
          .map(processor.convertRow)
          .sequence
    }
  }

}
