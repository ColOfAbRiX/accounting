package com.colofabrix.scala.accounting.etl

import cats.data.Validated.Invalid
import cats.implicits._
import cats.scalatest._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._
import csv._
import AllInputs._
import definitions._
import java.time.LocalDate
import org.scalatest.{ FlatSpec, Matchers }
import com.colofabrix.scala.accounting.etl.csv.inputs.BarclaysCsvProcessor

/**
 * Defines a mixin to provide test data
 */
trait InputTestData[T <: InputTransaction] {

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
  def convertedBadData: List[Invalid[String]]

}

/**
 * Defined the tests for all input conversions
 */
trait InputConversionSpec[T <: InputTransaction] extends FlatSpec with Matchers with ValidatedMatchers {
  this: InputTestData[T] =>

  import cats.data.Validated.Valid
  def printV[T](input: AValidated[List[T]]): Unit = {
    println(s"TYPE: $input")
    input match {
      case Valid(a) =>
        println("VALID")
        a.foreach(println)
      case Invalid(e) =>
        println("VALID")
    }
  }

  s"A VALID input data for ${name}" should "be converted into a valid result" in {
    val computedV = converter(this.sampleCorrectCsvData).ingestInput
    val expectedV = this.convertedCorrectData.aValid
    printV(computedV)
    computedV shouldBe valid
  }

  // s"A VALID input data for ${name}" should s"be converted into a sequence of ${name}Transaction" in {
  //   val computedV = converter(this.sampleCorrectCsvData).ingestInput
  //   val expectedV = this.convertedCorrectData.aValid
  //   (computedV, expectedV).mapN { (computed, expected) =>
  //     computed should contain theSameElementsInOrderAs (expected)
  //   }
  // }

  // s"An INVALID input data for ${name}" should "be converted into an invalid result" in {
  //   val computedV = converter(this.sampleBadCsvData).ingestInput
  //   computedV.leftMap(println)
  //   //val expectedV = this.convertedCorrectData.aValid
  //   //computedV shouldBe valid
  // }

  // s"An INVALID input data for ${name}" should "report correct conversion errors" in {
  //   val computedV = converter(this.sampleBadCsvData).ingestInput
  //   //val expectedV = this.convertedCorrectData.aValid
  //   //computedV shouldBe valid
  // }

}
