package com.colofabrix.scala.accounting.etl

import java.time.format.DateTimeFormatter
import java.time.LocalDate
import scala.annotation.implicitNotFound
import cats.data.Kleisli
import cats.implicits._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.utils.validation._

/**
 * Parser to transform record fields into JVM types
 */
trait FieldConverter[+A] {
  def parseField(field: String): AValidated[A]
}

object FieldConverter {

  /** A composable parser of fields to type A: given a input record it produces an validated A */
  type FieldBuilder[A] = Kleisli[AValidated, RawRecord, A]

  /** Type-safe method to parse a value given a function to extract what to parse from a RawRecord */
  @implicitNotFound("Couldn't find FieldConverter for type FieldConverter[${A}]")
  def parse[A](extract: RawRecord => String)(implicit parser: FieldConverter[A]): FieldBuilder[A] =
    Kleisli { record =>
      val extracted = TryE(extract(record)).leftMap { ex =>
        s"Exception on converting record $record: ${ex.toString}"
      }.toAValidated
      val parsed = parser.parseField _
      extracted andThen parsed
    }

  /** Method to create the default parser for the given type */
  def apply[A](f: String => A): FieldConverter[A] = new FieldConverter[A] {
    def parseField(field: String): AValidated[A] = {
      TryE(f(field)).leftMap { ex =>
        s"Exception on converting field '$field': ${ex.toString}"
      }.toAValidated
    }
  }

  /** Parser for result type "String" */
  implicit val stringParser: FieldConverter[String] = FieldConverter[String] {
    _.trim.toLowerCase.replaceAll("\\s+", " ")
  }

  /** Parser for result type "Int" */
  implicit val intParser: FieldConverter[Int] = FieldConverter[Int] {
    _.trim.toInt
  }

  /** Parser for result type "Double" */
  implicit val doubleParser: FieldConverter[Double] = FieldConverter[Double] {
    _.trim.toDouble
  }

  /** Parser for result type "BigDecimal" */
  implicit val bigDecimalParser: FieldConverter[BigDecimal] = FieldConverter[BigDecimal] { cell =>
    BigDecimal(cell.trim)
  }

  /** Parser for result type "LocalDate" */
  implicit def localDateParser(dateFormat: String): FieldConverter[LocalDate] = {
    FieldConverter[LocalDate] { cell =>
      LocalDate.parse(cell.trim, DateTimeFormatter.ofPattern(dateFormat))
    }
  }

  /** Parser for result type "Option[A]" */
  @implicitNotFound("Couldn't find FieldConverter for type FieldConverter[${A}]")
  implicit def optionParser[A](implicit aParser: FieldConverter[A]): FieldConverter[Option[A]] = {
    FieldConverter[Option[A]](aParser.parseField(_).toOption)
  }

  /** Parser for result type "List[A]" */
  @implicitNotFound("Couldn't find FieldConverter for type FieldConverter[${A}]")
  implicit def listParser[A](implicit aParser: FieldConverter[A]): FieldConverter[List[A]] = { cell =>
    cell.split(",").toList.traverse(aParser.parseField)
  }
}
