package com.colofabrix.scala.accounting.csv

import java.io.File
import cats.data._
import com.colofabrix.scala.accounting.csv.CsvRawTypeParser.CsvRowParser
import monix.reactive.Observable
import shapeless.Poly1


object CsvDefinitions {

  type CsvRow = List[String]

  type CsvStream = Observable[CsvRow]

  type CsvValidated[A] = ValidatedNec[Throwable, A]


  /**
    * Interface for a generic CSV reader that reads raw data
    */
  trait CsvReader {
    def readFile(file: File): CsvValidated[CsvStream]
  }


  /**
    * Type of CSV reader
    */
  trait CsvReaderType

  object CsvReaderType {
    /** Factory method to create a new reader from CsvReaderType */
    def apply(readerType: CsvReaderType): CsvReader = readerType match {
      case KantanCsvReaderType => new KantanCsvReader()
    }
  }


  /**
    * Represents an object that can convert CSV files into type A
    */
  trait CsvConverter[A] {
    /** Converts a Csv row into a BankTransaction */
    def convertRow(row: CsvRow): CsvValidated[A]
  }


  /**
    * A CSV cleaner for a specific Bank
    */
  trait CsvCleaner[A] {
    /** File cleanups specific of the Bank */
    def cleanFile(row: CsvStream): CsvStream
  }
}
