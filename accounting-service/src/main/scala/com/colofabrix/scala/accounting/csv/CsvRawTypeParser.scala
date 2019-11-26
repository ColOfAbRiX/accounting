package com.colofabrix.scala.accounting.csv

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util._
import com.colofabrix.scala.accounting.csv.CsvDefinitions.CsvValidated
import com.colofabrix.scala.accounting.utils.AccountingOps._


/**
  * Converter to transform csv fields into JVM types
  */
trait CsvRawTypeParser[A] {
  def parse(cell: String): CsvValidated[A]
}

object CsvRawTypeParser {

  type CsvCellParser[A] = String => CsvValidated[A]
  type CsvRowParser[A] = List[String] => CsvValidated[A]


  /** Type-safe method to parse a value given a function to extract what to parse from a row */
  def parse[A](extract: List[String] => String)(implicit parser: CsvRawTypeParser[A]): CsvRowParser[A] = {
    row =>
      val extracted = Try(extract(row)).toValidatedNec
      val parsed = parser.parse _
      extracted andThen parsed
  }


  /** Method to create the default parser for the given type */
  def apply[A](f: String => A): CsvRawTypeParser[A] = new CsvRawTypeParser[A] {
    def parse(cell: String): CsvValidated[A] = Try(f(cell)).toValidatedNec
  }


  /** String */
  implicit val stringParser: CsvRawTypeParser[String] = CsvRawTypeParser[String] {
    _.trim.toLowerCase
  }

  /** Int */
  implicit val intParser: CsvRawTypeParser[Int] = CsvRawTypeParser[Int] {
    _.trim.toInt
  }

  /** BigDecimal */
  implicit val bigDecimalParser: CsvRawTypeParser[BigDecimal] = CsvRawTypeParser[BigDecimal] {
    BigDecimal(_)
  }

  /** LocalDate */
  implicit def localDateParser(dateFormat: String): CsvRawTypeParser[LocalDate] = {
    CsvRawTypeParser[LocalDate] { cell =>
      LocalDate.parse(cell, DateTimeFormatter.ofPattern(dateFormat))
    }
  }

  /** Option[A] */
  implicit def optionParser[A](implicit aParser: CsvRawTypeParser[A]): CsvRawTypeParser[Option[A]] = {
    CsvRawTypeParser[Option[A]](aParser.parse(_).toOption)
  }
}
