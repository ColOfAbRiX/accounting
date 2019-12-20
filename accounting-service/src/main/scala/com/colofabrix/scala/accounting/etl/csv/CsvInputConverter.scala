package com.colofabrix.scala.accounting.etl.csv

import cats._
import cats.implicits._
import java.io.File
import com.colofabrix.scala.accounting.etl._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.AValidation._
import com.colofabrix.scala.accounting.etl.InputDefinitions._
import shapeless._
import cats.data.Validated.Invalid
import cats.data.Validated.Valid

/**
 * Processes a CSV file
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
class CsvInputConverter[T <: InputTransaction](reader: CsvReader)(implicit val converter: CsvProcessor[T])
    extends InputConverter[File, T] {

  def ingestInput(input: File): AValidated[List[T]] = {
    reader.read(input) match {
      case i @ Invalid(_)  => i
      case Valid(rawInput) => converter
        .filterFile(rawInput)
        .map(converter.convertRow)
        .sequence
    }
  }
}
