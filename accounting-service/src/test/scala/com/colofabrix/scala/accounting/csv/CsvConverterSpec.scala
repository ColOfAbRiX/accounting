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
    def filterFile(file: CsvFile): AValidated[CsvFile] = file.aValid

    def convertRow(row: CsvRow): AValidated[Person] =
      convert(row) {
        parse[String](r => r(0) + " " + r(1)) ::
        parse[Int](r => r(2)) ::
        HNil
      }
  }

  "A CSV row" should "be converted to case class" in {
    val testRow  = List("fabrizio", "colonna", "34")
    val computed = PersonConverter.convertRow(testRow)
    val expected = Person("fabrizio colonna", 34).aValid
    computed should equal(expected)
  }

  "A badly formatted CSV row" should "be invalid" in {
    val testRow  = List(null, "colonna", "abcd")
    val computed = PersonConverter.convertRow(testRow)
    val expected =
      "Exception on parsing CSV cell 'abcd': java.lang.NumberFormatException: For input string: \"abcd\"".aInvalid
    computed should equal(expected)
  }
}
