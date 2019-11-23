package com.colofabrix.scala.accounting.csv

import java.io.File
import cats.data._
import monix.reactive.Observable


object CsvDefinitions {

  type CsvRow = List[String]

  type CsvStream = Observable[CsvRow]

  type CsvValidated[A] = Validated[Throwable, A]


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
    /**
      * Factory method to create a new reader from CsvReaderType
      */
    def apply(readerType: CsvReaderType): CsvReader = readerType match {
      case KantanCsvReaderType => new KantanCsvReader()
    }
  }


  /**
    * Represents an object that can convert CSV files into type A
    */
  trait CsvConverter[A] {
    /**
      * Sets the date format used by the Bank CSv file
      */
    def dateFormat: String

    /**
      * Converts a Csv row into a BankTransaction
      */
    def convertRow(row: CsvRow): CsvValidated[A]
  }


  /**
    * A CSV cleaner for a specific Bank
    */
  trait CsvCleaner[A] {
    /**
      * File cleanups specific of the Bank
      */
    def cleanFile(row: CsvStream): CsvStream
  }


  /**
    * A field in a Csv file
    */
  case class CsvFieldDef[A](
      index: Int,
      name: String,
      convert: String => CsvValidated[A]
  )
}
