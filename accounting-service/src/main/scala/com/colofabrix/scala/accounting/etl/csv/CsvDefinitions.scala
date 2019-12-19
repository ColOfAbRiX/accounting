package com.colofabrix.scala.accounting.etl.csv

import java.io.File
import com.colofabrix.scala.accounting.etl.InputDefinitions._
import com.colofabrix.scala.accounting.utils.AValidation.AValidated
import com.colofabrix.scala.accounting.model.InputTransaction

/**
  * Interface for a generic CSV reader that reads raw data
  */
trait CsvReader {
  def read(file: File): AValidated[RawInput]
}

/**
 * Processes a CSV file
 */
trait CsvProcessor[T <: InputTransaction] {
  /** Converts a Csv row into a BankTransaction */
  def filterFile(file: RawInput): AValidated[CsvFile] = {
  /** Converts a Csv row into a BankTransaction */
  def convertRow(row: RawRecord): AValidated[T]
}


// /**
//   * Type of CSV readers
//   */
// sealed trait CsvReaderType
// final case object KantanCsvReaderType extends CsvReaderType

// object CsvReaderType {

//   /** Factory method to create a new reader from CsvReaderType */
//   def apply(readerType: CsvReaderType): CsvReader = readerType match {
//     case KantanCsvReaderType => new KantanCsvReader()
//   }

// }
