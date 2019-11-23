package com.colofabrix.scala.accounting.csv

import java.io.File
import scala.util._
import cats.data._
import cats.implicits._
import com.colofabrix.scala.accounting.csv.CsvDefinitions._
import monix.reactive.Observable


/**
 * Kantan CSV reader type
 */
final case object KantanCsvReaderType extends CsvReaderType


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
    tryReader.toEither.toValidated
  }

}
