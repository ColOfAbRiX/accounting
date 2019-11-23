package com.colofabrix.scala.accounting.csv

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util._
import cats.implicits._
import com.colofabrix.scala.accounting.csv.CsvDefinitions.CsvValidated


/**
  * Converter to transform csv fields into JVM types
  */
trait CsvRawTypeParser[A] {
  def parse(cell: String): CsvValidated[A]
}

object CsvRawTypeParser {

  /** Method to create the default parser for the given type */
  def apply[A](f: String => A): CsvRawTypeParser[A] = (cell: String) => {
    Try(f(cell)).toEither.toValidated
  }

  /** Type-safe method to parse a value */
  def parse[A](implicit parser: CsvRawTypeParser[A]): String => CsvValidated[A] = {
    parser.parse
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
