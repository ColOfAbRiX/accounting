package com.colofabrix.scala.accounting.model

import fs2._
import java.io.File
import cats.data._
import cats.data.Validated._
import cats.implicits._
import kantan.csv._
import kantan.csv.ops._
import scala.util._
import GenericCsvReader._
import monix.reactive.Observable


trait GenericCsvReader {
  def readFile(file: File): ValidatedCsvFile[String]
}

object GenericCsvReader {

  type CsvRow[A] = List[A]
  type ValidatedCsvRow[A] = Validated[Exception, CsvRow[A]]     // Validated[Exception, List[A]]
  type CsvFile[A] = Observable[ValidatedCsvRow[A]]              // Observable[Validated[Exception, List[A]]]
  type ValidatedCsvFile[A] = Validated[Exception, CsvFile[A]]   // Validated[Exception, Observable[Validated[Exception, List[A]]]]

}


class KantanCsvReader extends GenericCsvReader {

  def readFile(file: File): ValidatedCsvFile[String] = {
    val rowsIterable = file.asCsvReader[List[String]](rfc).toIterable
    Observable
      .fromIterable(rowsIterable)
      .map(_.toValidated)
      .valid
  }

}
