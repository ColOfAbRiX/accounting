package com.colofabrix.scala.accounting.etl

import cats.data.Validated.Invalid
import cats.implicits._
import cats.scalatest._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._
import csv._
import definitions._
import java.time.LocalDate
import org.scalatest.{ FlatSpec, Matchers }

/**
 * Defines a mixin to provide test data
 */
trait InputTestData[T <: InputTransaction] {
  implicit val csvProcessor: CsvProcessor[T]

  def date(year: Int, month: Int, day: Int): LocalDate = LocalDate.of(year, month, day)

  def converter(data: RawInput)(implicit processor: CsvProcessor[T]): InputConverter[T] = {
    new CsvInputConverter[T](new DummyCsvReader(data), processor)
  }

  def name: String = this.getClass.getSimpleName.replaceAll("""InputConversion.*$""", "")

  /** Test dataset of correct CSV data */
  def sampleCorrectCsvData: List[RawRecord]
  /** Expected result for conversion of sampleCorrectCsvData */
  def convertedCorrectData: List[T]
  /** Test dataset of invalid CSV data */
  def sampleBadCsvData: List[RawRecord]
  /** Expected result for conversion of sampleBadCsvData */
  def convertedBadData: List[String]

}

/**
 * Defined the tests for all input conversions
 */
trait InputConversionSpec[T <: InputTransaction] extends FlatSpec with Matchers with ValidatedMatchers {
  this: InputTestData[T] =>

  private def printV[T](input: AValidated[List[T]]): Unit = {
    import cats.data.Validated.{Valid, Invalid}
    println("--------")
    input match {
      case Valid(a) =>
        println(s"VALID")
        a.foreach(println)
      case Invalid(e) =>
        println(s"INVALID")
        e.iterator.foreach(println)
      }
    println("--------")
  }

  private def compare[A, B](expected: List[A], computed: List[A]) = {
    expected.zipAll(computed, "A", "B").foreach { case (exp, comp) =>
      println(s"COMPUTED: $comp")
      println(s"EXPECTED: $exp")
      if (comp != exp) println("DIFFERENT")
      println("")
    }
  }

  s"A VALID input data for ${name}" should "be converted into a valid result" in {
    val computedV = converter(this.sampleCorrectCsvData).ingestInput
    computedV shouldBe valid
  }

  s"A VALID input data for ${name}" should s"be converted into a sequence of ${name}Transaction" in {
    val computedV = converter(this.sampleCorrectCsvData).ingestInput
    val expectedV = this.convertedCorrectData.aValid
    (computedV, expectedV).mapN { (computed, expected) =>
      computed should contain theSameElementsInOrderAs (expected)
    }
  }

  s"An INVALID input data for ${name}" should "be converted into an invalid result" in {
    val computedV = converter(this.sampleBadCsvData).ingestInput
    computedV shouldBe invalid
  }

  s"An INVALID input data for ${name}" should "report correct conversion errors" in {
    val computedV = converter(this.sampleBadCsvData).ingestInput
    val computed = computedV
      .toEither
      .left
      .toOption
      .get
      .toList
    computed should contain theSameElementsAs (this.convertedBadData)
  }

}
