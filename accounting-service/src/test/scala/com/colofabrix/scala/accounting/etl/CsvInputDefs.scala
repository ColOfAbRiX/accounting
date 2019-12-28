package com.colofabrix.scala.accounting.etl

import java.time.LocalDate
import cats.implicits._
import cats.scalatest._
import com.colofabrix.scala.accounting.etl.csv._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._
import com.colofabrix.scala.accounting.utils.DebugHelpers
import org.scalatest._
import cats.effect._

/**
 * Defines a mixin to provide test data
 */
trait InputTestData[T <: InputTransaction] {
  def date(year: Int, month: Int, day: Int): LocalDate = LocalDate.of(year, month, day)
  def name: String = this.getClass.getSimpleName.replaceAll("""InputConversion.*$""", "")
  def read(data: List[RawRecord]): VRawInput[IO] = new IterableCsvReader(this.sampleCorrectCsvData).read

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
 * Defines the tests for all input conversions
   */
trait InputConversionSpec[T <: InputTransaction]
    extends FlatSpec
    with Matchers
    with ValidatedMatchers
    with DebugHelpers {
  this: InputTestData[T] =>

  /** Needed to provide the processor to convert the data */
  implicit val csvProcessor: CsvProcessor[T]

  s"A VALID input data for ${name}" should "be converted into a valid result" in {
    val result = read(this.sampleCorrectCsvData).through(csvProcessor.process)
    withValidatedIoStream(result) { computed =>
      computed.foreach(_ shouldBe valid)
    }
  }

  // s"A VALID input data for ${name}" should s"be converted into a sequence of ${name}Transaction" in {
  //   val result = read(this.sampleCorrectCsvData).through(csvProcessor.process)
  //   val expectedV = this.convertedCorrectData.aValid
  //   withValidatedIoStream(result) { computedV =>
  //     (computedV, expectedV).mapN { (computed, expected) =>
  //       computed should contain theSameElementsInOrderAs (expected)
  //     }
  //   }
  // }

  s"An INVALID input data for ${name}" should "be converted into an invalid result" in {
    val result = read(this.sampleBadCsvData).through(csvProcessor.process)
    withValidatedIoStream(result) { computed =>
      computed.foreach(_ shouldBe invalid)
    }
  }

  // s"An INVALID input data for ${name}" should "report correct conversion errors" in {
  //   val computedV = converter(this.sampleBadCsvData).ingestInput
  //   val computed  = computedV.toEither.left.get.toList
  //   computed should contain theSameElementsAs (this.convertedBadData)
  // }

}
