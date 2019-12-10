package com.colofabrix.scala.accounting.csv

import java.io.File
import com.colofabrix.scala.accounting.utils.AValidation._
import monix.reactive.Observable


object CsvDefinitions {

  /** A line of the Csv file, simply a List[String */
  type CsvRow = List[String]

  /** A CsvStream is a steam of CsvRows */
  type CsvStream = Observable[CsvRow]

  /**
    * Interface for a generic CSV reader that reads raw data
    */
  trait CsvReader {
    def readFile(file: File): AValidated[CsvStream]
  }

  /**
    * Type of CSV reader
    */
  sealed trait CsvReaderType
  final case object KantanCsvReaderType extends CsvReaderType

  object CsvReaderType {
    /** Factory method to create a new reader from CsvReaderType */
    def apply(readerType: CsvReaderType): CsvReader = readerType match {
      case KantanCsvReaderType => new KantanCsvReader()
    }
  }
}
