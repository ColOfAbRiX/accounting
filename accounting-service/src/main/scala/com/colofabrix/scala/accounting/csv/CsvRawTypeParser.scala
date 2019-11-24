package com.colofabrix.scala.accounting.csv

import cats.implicits._
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util._
import com.colofabrix.scala.accounting.csv.CsvDefinitions.CsvValidationResult


/**
  * Converter to transform csv fields into JVM types
  */
trait CsvRawTypeParser[A] {
  def parse(cell: String): CsvValidationResult[A]
}

object CsvRawTypeParser {
  /** Method to create the default parser for the given type */
  def apply[A](f: String => A): CsvRawTypeParser[A] = new CsvRawTypeParser[A] {
    override def parse(cell: String): CsvValidationResult[A] = {
      Try(f(cell)).toEither.toValidatedNec
    }
  }

  /** Type-safe method to parse a value */
  def parse[A](implicit parser: CsvRawTypeParser[A]): String => CsvValidationResult[A] = {
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
