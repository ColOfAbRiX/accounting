package com.colofabrix.scala.accounting.readers

import cats.data._
import cats.data.Validated._
import cats.implicits._
import java.io.File
import monix.reactive.Observable
import scala.util._

/**
 * Interface for a generic CSV reader that reads raw data
 */
trait CsvReader {
  def readFile(file: File): Validated[Throwable, Observable[List[String]]]
}

/**
 * Kantan CSV Reader
 */
class KantanCsvReader extends CsvReader {
  import kantan.csv._
  import kantan.csv.ops._

  def readFile(file: File): Validated[Throwable, Observable[List[String]]] = {
    val tryReader = Try {
      val csvReader = file.asUnsafeCsvReader[List[String]](rfc)
      Observable.fromIterable(csvReader.toIterable)
    }
    tryReader
      .toEither
      .toValidated
  }

}
