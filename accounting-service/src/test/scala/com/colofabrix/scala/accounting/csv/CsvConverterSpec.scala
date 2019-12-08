package com.colofabrix.scala.accounting.csv

import cats.implicits._
import org.scalatest.{FlatSpec, Matchers}
import com.colofabrix.scala.accounting.csv.CsvDefinitions._
import com.colofabrix.scala.accounting.csv.CsvTypeParser._
import shapeless.HNil


class CsvConverterSpec extends FlatSpec with Matchers {

  case class Person(name: String, surname: String)

  class PersonConverter extends CsvConverter[Person] {
    override def filterFile(file: CsvStream): CsvValidated[CsvStream] = file.validNec[Throwable]

    override def convertRow(row: CsvRow): CsvValidated[Person] = {
      val parsers =
        parse[String](r => r(0)) ::
        parse[String](r => r(1)) ::
        HNil

      val factory = Person.apply _

      convertRowGeneric(parsers, row, factory)
    }
  }

  "A CSV row" should "be converted to case class" in {
    val computed = new PersonConverter().convertRow(List("fabrizio", "colonna"))
    val expected = Person("fabrizio", "colonna").validNec[Throwable]
    computed should equal(expected)
  }

}
