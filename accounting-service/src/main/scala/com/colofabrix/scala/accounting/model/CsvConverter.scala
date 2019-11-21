package com.colofabrix.scala.accounting.model

import scala.util._
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import cats.implicits._
import com.colofabrix.scala.accounting.model.CsvDefinitions._


trait CsvConverter[+A] {
  /**
   * Sets the date format used by the Bank CSv file
   */
  def dateFormat: String

  /**
   * Converts a Csv row into a BankTransaction
   */
  def convertRow(row: List[String]): CsvValidated[A]

  def parseLocalDate(input: String): CsvValidated[LocalDate] = {
    Try(LocalDate.parse(input, DateTimeFormatter.ofPattern(dateFormat))).toEither.toValidated
  }

  def parseBigDecimal(input: String): CsvValidated[BigDecimal] = {
    Try(BigDecimal(input)).toEither.toValidated
  }

  def parseInt(input: String): CsvValidated[Int] = {
    Try(input.toInt).toEither.toValidated
  }

  def parseString(input: String): CsvValidated[String] = {
    input.valid
  }
}
