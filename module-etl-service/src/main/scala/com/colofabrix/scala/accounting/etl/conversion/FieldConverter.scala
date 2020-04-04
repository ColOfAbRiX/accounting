package com.colofabrix.scala.accounting.etl.conversion

import cats.data.Kleisli
import cats.implicits._
import cats.Show
import com.colofabrix.scala.accounting.utils.validation._
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import scala.annotation.implicitNotFound
import scala.util.Try

/**
 * Parser to transform record fields into JVM types
 */
trait FieldConverter[-I, +O] {
  def parseField(field: I): AValidated[O]
}

object FieldConverter {

  /** A composable parser of fields to type A: given a input record it produces an validated A */
  type FieldBuilder[-I, O] = Kleisli[AValidated, List[I], O]

  /** Type-safe method to parse a value given a function that extracts what to parse from an input value */
  @implicitNotFound("Couldn't find FieldConverter for type FieldConverter[${I}, ${O}]")
  def iParseO[I, O](extract: List[I] => I)(implicit P: FieldConverter[I, O]): FieldBuilder[I, O] =
    Kleisli { record =>
      val extracted = Try(extract(record))
        .toEither
        .leftMap { ex =>
          val r = Option(record).map(_.toString).getOrElse("null")
          s"Exception on converting record $r: ${ex.toString}"
        }
        .toAValidated

      extracted andThen P.parseField
    }

  /** Type-safe method to parse a value given a function that extracts what to parse from a String value */
  @implicitNotFound("Couldn't find FieldConverter for type FieldConverter[String, ${O}]")
  def sParse[O](e: List[String] => String)(implicit P: FieldConverter[String, O]): FieldBuilder[String, O] = {
    iParseO[String, O](e)(P)
  }

  /** Method to create the default parser for the given type */
  def apply[I, O](f: I => O)(implicit S: Show[I]): FieldConverter[I, O] = new FieldConverter[I, O] {
    def parseField(field: I): AValidated[O] = {
      Try(f(field))
        .toEither
        .leftMap { ex =>
          val f = Option(field).map(S.show).getOrElse("null")
          s"Exception on converting field '$f': ${ex.toString}"
        }
        .toAValidated
    }
  }

  //  TYPECLASS INSTANCES  //

  /** Parser for result type "String" */
  implicit val stringParser: FieldConverter[String, String] = FieldConverter[String, String] {
    _.toString // Using .toString to raise an exception when the input is null
  }

  /** Parser for result type "Int" */
  implicit val intParser: FieldConverter[String, Int] = FieldConverter[String, Int] {
    _.trim.toInt
  }

  /** Parser for result type "Double" */
  implicit val doubleParser: FieldConverter[String, Double] = FieldConverter[String, Double] {
    _.trim.toDouble
  }

  /** Parser for result type "BigDecimal" */
  implicit val bigDecimalParser: FieldConverter[String, BigDecimal] = FieldConverter[String, BigDecimal] { cell =>
    BigDecimal(cell.trim)
  }

  /** Parser for result type "LocalDate" */
  @SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion"))
  implicit def localDateParser(dateFormat: String): FieldConverter[String, LocalDate] = {
    FieldConverter[String, LocalDate] { cell =>
      LocalDate.parse(cell.trim, DateTimeFormatter.ofPattern(dateFormat))
    }
  }

  /** Parser for result type "Option[A]" */
  @implicitNotFound("Couldn't find FieldConverter for type FieldConverter[${A}]")
  implicit def optionParser[A](implicit aParser: FieldConverter[String, A]): FieldConverter[String, Option[A]] = {
    FieldConverter[String, Option[A]](aParser.parseField(_).toOption)
  }

  /** Parser for result type "List[A]" */
  @implicitNotFound("Couldn't find FieldConverter for type FieldConverter[${A}]")
  implicit def listParser[A](implicit aParser: FieldConverter[String, A]): FieldConverter[String, List[A]] = { cell =>
    cell.split(",").toList.traverse(aParser.parseField)
  }
}
