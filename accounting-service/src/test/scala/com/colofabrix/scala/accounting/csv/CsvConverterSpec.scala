package com.colofabrix.scala.accounting.csv

import cats.implicits._
import org.scalatest.{FlatSpec, Matchers}
import com.colofabrix.scala.accounting.csv.CsvDefinitions._
import com.colofabrix.scala.accounting.csv.CsvFieldParser._
import com.colofabrix.scala.accounting.utils.AValidation.AValidated
import com.colofabrix.scala.accounting.utils.AValidation._
import shapeless._


class CsvConverterSpec extends FlatSpec with Matchers {

  case class Person(name: String, age: Int)

  object PersonConverter extends CsvConverter[Person] {
    override def filterFile(file: CsvStream): AValidated[CsvStream] = file.aValid

    override def convertRow(row: CsvRow): AValidated[Person] = {
      convert(row) {
        parse[String](r => r(0) + " " + r(1)) ::
        parse[Int]   (r => r(2)) ::
        HNil
      }
    }
  }

  "A CSV row" should "be converted to case class" in {
    val computed = PersonConverter.convertRow(List("fabrizio", "colonna", "34"))
    val expected = Person("fabrizio colonna", 34).aValid
    computed should equal(expected)
  }

  "A badly formatted CSV row" should "be converted to an invalid value" in {
    val computed = PersonConverter.convertRow(List(null, "colonna", "abcd"))
    val expected = Person("fabrizio colonna", 34).aValid
    computed should equal(expected)
  }
}
