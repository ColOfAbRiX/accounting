package com.colofabrix.scala.accounting.model

import fs2._
import java.io.File
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import kantan.csv._
import kantan.csv.ops._
import scala.util._
import BankCsvInterpreterUtils._


trait BankFileConverter[A <: BankCsvRow] {
  import BankCsvInterpreter._

  def csvConfig: CsvConfiguration
  def dateFormat: String
  def adaptFile(file: CsvFile[String]): CsvFile[String]
  def convertRow(row: CsvRow[String]): Try[A]

  def parseLocalDate(index: Int)(implicit row: List[String]): Try[LocalDate] = Try {
    LocalDate.parse(row(index).trim, DateTimeFormatter.ofPattern(dateFormat))
  }

  def parseBigDecimal(index: Int)(implicit row: List[String]): Try[BigDecimal] = Try {
    BigDecimal(row(index).trim)
  }

  def parseString(index: Int)(implicit row: List[String]): Try[String] = Try {
    row(index).trim
  }

  def parseInt(index: Int)(implicit row: List[String]): Try[Int] = Try {
    row(index).trim.toInt
  }
}



object BankCsvInterpreter {

  def readCsv[A <: BankCsvRow: BankFileConverter](file: File) = {
    val converter = implicitly[BankFileConverter[A]]
    val reader = file.asCsvReader[List[String]](converter.csvConfig)
    Stream.unfold(reader) { i =>
      if( i.hasNext ) Some((i.next(), i)) else None
    }
  }

  def basicRowCleaning(row: CsvRow[String]): CsvRow[String] = {
    for {
      csvCell <- row
    } yield {
      csvCell.trim().toLowerCase()
    }
  }

}

object BankCsvInterpreterUtils {

  type CsvRow[A] = List[A]
  type CsvFile[A] = Stream[Pure, A]

}
