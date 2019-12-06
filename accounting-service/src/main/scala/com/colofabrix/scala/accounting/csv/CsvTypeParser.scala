package com.colofabrix.scala.accounting.csv

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util._
import cats.data.Kleisli
import com.colofabrix.scala.accounting.csv.CsvDefinitions.{CsvRow, CsvValidated}
import com.colofabrix.scala.accounting.utils.AccountingOps._


/**
  * Parser to transform Csv fields into JVM types
  */
trait CsvTypeParser[A] {
  def parseCell(cell: String): CsvValidated[A]
}

object CsvTypeParser {

  type CsvRowParser[A] = Kleisli[CsvValidated, CsvRow, A]

  /** Type-safe method to parse a value given a function to extract what to parse from a CsvRow */
  def parse[A](extract: CsvRow => String)(implicit parser: CsvTypeParser[A]): CsvRowParser[A] =
    Kleisli { row =>
      val extracted = Try(extract(row)).toValidatedNec
      val parsed = parser.parseCell _
      extracted andThen parsed
    }


  /** Method to create the default parser for the given type */
  def apply[A](f: String => A): CsvTypeParser[A] = new CsvTypeParser[A] {
    def parseCell(cell: String): CsvValidated[A] = Try(f(cell)).toValidatedNec
  }


  /** Parser for result type "String" */
  implicit val stringParser: CsvTypeParser[String] = CsvTypeParser[String] {
    _.trim.toLowerCase.replaceAll("\\s+", " ")
  }

  /** Parser for result type "Int" */
  implicit val intParser: CsvTypeParser[Int] = CsvTypeParser[Int] {
    _.trim.toInt
  }

  /** Parser for result type "Double" */
  implicit val doubleParser: CsvTypeParser[Double] = CsvTypeParser[Double] {
    _.trim.toDouble
  }

  /** Parser for result type "BigDecimal" */
  implicit val bigDecimalParser: CsvTypeParser[BigDecimal] = CsvTypeParser[BigDecimal] {
    BigDecimal(_)
  }

  /** Parser for result type "LocalDate" */
  implicit def localDateParser(dateFormat: String): CsvTypeParser[LocalDate] = {
    CsvTypeParser[LocalDate] { cell =>
      LocalDate.parse(cell, DateTimeFormatter.ofPattern(dateFormat))
    }
  }

  /** Parser for result type "Option[A]" */
  implicit def optionParser[A](implicit aParser: CsvTypeParser[A]): CsvTypeParser[Option[A]] = {
    CsvTypeParser[Option[A]](aParser.parseCell(_).toOption)
  }
}
