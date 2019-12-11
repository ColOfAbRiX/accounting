package com.colofabrix.scala.accounting.csv

import java.io.File
import com.colofabrix.scala.accounting.csv.CsvDefinitions.CsvStream
import com.colofabrix.scala.accounting.utils.AValidation.AValidated


/**
  * Interface for a generic CSV reader that reads raw data
  */
trait CsvReader {
  def readFile(file: File): AValidated[CsvStream]
}

/**
  * Type of CSV readers
  */
sealed trait CsvReaderType
case object KantanCsvReaderType extends CsvReaderType

object CsvReaderType {
  /** Factory method to create a new reader from CsvReaderType */
  def apply(readerType: CsvReaderType): CsvReader = readerType match {
    case KantanCsvReaderType => new KantanCsvReader()
  }
}
