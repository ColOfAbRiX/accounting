package com.colofabrix.scala.accounting.csv

import cats.data.Kleisli
import cats.implicits._
import com.colofabrix.scala.accounting.csv.CsvDefinitions.CsvRow
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import scala.annotation.implicitNotFound
import scala.util._
import com.colofabrix.scala.accounting.utils.AValidation._


/**
  * Parser to transform Csv fields into JVM types
  */
trait CsvFieldParser[A] {
  def parseCell(cell: String): AValidated[A]
}

object CsvFieldParser {

  type CsvRowParser[A] = Kleisli[AValidated, CsvRow, A]

  /** Type-safe method to parse a value given a function to extract what to parse from a CsvRow */
  @implicitNotFound("Couldn't find CsvFieldParser for type CsvFieldParser[${A}]")
  def parse[A](extract: CsvRow => String)(implicit parser: CsvFieldParser[A]): CsvRowParser[A] =
    Kleisli { row =>
      val extracted = Try(extract(row)).toAValidated
      val parsed = parser.parseCell _
      extracted andThen parsed
    }


  /** Method to create the default parser for the given type */
  def apply[A](f: String => A): CsvFieldParser[A] = new CsvFieldParser[A] {
    def parseCell(cell: String): AValidated[A] = Try(f(cell)).toAValidated
  }


  /** Parser for result type "String" */
  implicit val stringParser: CsvFieldParser[String] = CsvFieldParser[String] {
    _.trim.toLowerCase.replaceAll("\\s+", " ")
  }

  /** Parser for result type "Int" */
  implicit val intParser: CsvFieldParser[Int] = CsvFieldParser[Int] {
    _.trim.toInt
  }

  /** Parser for result type "Double" */
  implicit val doubleParser: CsvFieldParser[Double] = CsvFieldParser[Double] {
    _.trim.toDouble
  }

  /** Parser for result type "BigDecimal" */
  implicit val bigDecimalParser: CsvFieldParser[BigDecimal] = CsvFieldParser[BigDecimal] {
    BigDecimal(_)
  }

  /** Parser for result type "LocalDate" */
  implicit def localDateParser(dateFormat: String): CsvFieldParser[LocalDate] = {
    CsvFieldParser[LocalDate] { cell =>
      LocalDate.parse(cell, DateTimeFormatter.ofPattern(dateFormat))
    }
  }

  /** Parser for result type "Option[A]" */
  @implicitNotFound("Couldn't find CsvFieldParser for type CsvFieldParser[${A}]")
  implicit def optionParser[A](implicit aParser: CsvFieldParser[A]): CsvFieldParser[Option[A]] = {
    CsvFieldParser[Option[A]](aParser.parseCell(_).toOption)
  }

  /** Parser for result type "List[A]" */
  @implicitNotFound("Couldn't find CsvFieldParser for type CsvFieldParser[${A}]")
  implicit def listParser[A](implicit aParser: CsvFieldParser[A]): CsvFieldParser[List[A]] = { cell =>
    cell.split(",").toList.traverse(aParser.parseCell)
  }
}
