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
trait FieldConverter[+O] {
  def parseField(field: String): AValidated[O]
}

object FieldConverter {

  /** A composable parser of fields to type A: given a input record it produces an validated A */
  type FieldBuilder[O] = Kleisli[AValidated, List[String], O]

  /** Type-safe method to parse a value given a function that extracts what to parse from an input value */
  @implicitNotFound("Couldn't find FieldConverter for type FieldConverter[${I}, ${O}]")
  def parse[O](extract: List[String] => String)(implicit P: FieldConverter[O]): FieldBuilder[O] =
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

  /** Method to create the default parser for the given type */
  def apply[O](f: String => O)(implicit S: Show[String]): FieldConverter[O] = new FieldConverter[O] {
    def parseField(field: String): AValidated[O] = {
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
  implicit val stringParser: FieldConverter[String] = FieldConverter[String] {
    _.toString // Using .toString to raise an exception when the input is null
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
  @SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion"))
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
