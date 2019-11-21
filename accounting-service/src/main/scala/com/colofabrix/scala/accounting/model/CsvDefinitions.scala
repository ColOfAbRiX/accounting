package com.colofabrix.scala.accounting.model

import cats.data._
import java.io.File
import monix.reactive.Observable

object CsvDefinitions {

  type CsvValidated[A] = Validated[Throwable, A]


  /**
    * A field in a Csv file
    */
  case class CsvField[A](
      name: String,
      index: Int,
      convert: String => CsvValidated[A]
  )


  /**
    * Interface for a generic CSV reader that reads raw data
    */
  trait CsvReader {
    def readFile(file: File): CsvValidated[Observable[List[String]]]
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

}
