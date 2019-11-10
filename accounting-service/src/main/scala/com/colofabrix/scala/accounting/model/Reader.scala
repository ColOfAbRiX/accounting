package com.colofabrix.scala.accounting.model

import fs2._
import cats._
import cats.implicits._
import java.io.File
import kantan.csv.ops._
import kantan.csv.java8._
import kantan.csv.generic._
import kantan.csv.{ CsvReader => KCsvReader, ReadResult, rfc }

sealed trait Reader {
}

object KantanCsvReader extends Reader {
  type CsvReader[A] = KCsvReader[ReadResult[A]]

  def readStream[A: BankCsvReader](file: File): Stream[Pure, Either[Exception, A]] = {
    Stream.unfold(read[A](file)) { i =>
      if( i.hasNext ) Some(i.next(), i) else None
    }
  }

  def read[A: BankCsvReader](file: File): CsvReader[A] = {
    implicitly[BankCsvReader[A]].reader(file)
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
