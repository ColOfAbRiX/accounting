package com.colofabrix.scala.accounting.model

import kantan.csv.{ CsvReader => KantanCsvReader, ReadResult, rfc }
import java.io.File
import kantan.csv.ops._
import kantan.csv.java8._
import kantan.csv.generic._

object Readers {
  type CsvReader[A] = KantanCsvReader[ReadResult[A]]

  def read[A](file: File)(implicit reader: BankCsvReader[A]): CsvReader[A] = {
    reader.reader(file)
  }

  sealed trait BankCsvReader[A] {
    def reader(file: File): CsvReader[A]
  }

  implicit def barclayReader: BankCsvReader[BarclaysRow] = new BankCsvReader[BarclaysRow] {
    def reader(file: File): CsvReader[BarclaysRow] = {
      file.asCsvReader[BarclaysRow](rfc.withoutHeader)
    }
  }

  implicit def halifaxReader: BankCsvReader[HalifaxRow] = new BankCsvReader[HalifaxRow] {
    def reader(file: File): CsvReader[HalifaxRow] = {
      file.asCsvReader[HalifaxRow](rfc.withoutHeader)
    }
  }

  implicit def starlingReader: BankCsvReader[StarlingRow] = new BankCsvReader[StarlingRow] {
    def reader(file: File): CsvReader[StarlingRow] = {
      file.asCsvReader[StarlingRow](rfc.withoutHeader)
    }
  }

  implicit def amexReader: BankCsvReader[AmexRow] = new BankCsvReader[AmexRow] {
    def reader(file: File): CsvReader[AmexRow] = {
      file.asCsvReader[AmexRow](rfc.withoutHeader)
    }
  }
}
