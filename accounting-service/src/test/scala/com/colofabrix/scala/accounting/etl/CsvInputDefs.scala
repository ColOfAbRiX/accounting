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

/**
 * Defines a mixin to provide test data
 */
trait InputTestData[T <: InputTransaction] {

  def name: String = this.getClass.getSimpleName.replaceAll("""InputConversion.*$""", "")
  def date(year: Int, month: Int, day: Int): LocalDate = LocalDate.of(year, month, day)
  def converter(data: RawInput): InputConverter[T] = new CsvInputConverter[T](new DummyCsvReader(data))

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

  s"The input data for ${name}" should "be converted into a valid result" in {
    val computedV = converter(this.sampleCorrectCsvData).ingestInput
    val expectedV = this.convertedCorrectData.aValid
    computedV shouldBe valid
  }

  s"The input data for ${name}" should s"be converted into a sequence of ${name}Transaction" in {
    val computedV = converter(this.sampleCorrectCsvData).ingestInput
    val expectedV = this.convertedCorrectData.aValid
    (computedV, expectedV).mapN { (computed, expected) =>
      computed should contain theSameElementsInOrderAs (expected)
    }
  }

  s"The bad input data for ${name}" should "be converted into an invalid result" in {
    val computedV = converter(this.sampleBadCsvData).ingestInput
    computedV.leftMap(println)
    //val expectedV = this.convertedCorrectData.aValid
    //computedV shouldBe valid
  }

  s"The bad input data for ${name}" should "report correct conversion errors" in {
    val computedV = converter(this.sampleBadCsvData).ingestInput
    //val expectedV = this.convertedCorrectData.aValid
    //computedV shouldBe valid
  }

}
