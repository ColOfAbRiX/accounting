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

/**
 * Defines a mixin to provide test data
 */
trait InputTestData[T <: InputTransaction] {
  def date(year: Int, month: Int, day: Int): LocalDate = LocalDate.of(year, month, day)

  def converter(data: RawInput)(implicit processor: CsvProcessor[T]): InputConverter[T] = {
    new CsvInputConverter[T](new IterableCsvReader(data.toList), processor)
  }

  def name: String = this.getClass.getSimpleName.replaceAll("""InputConversion.*$""", "")

  /** Needed to provide the processor to convert the data */
  implicit val csvProcessor: CsvProcessor[T]
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
// trait InputConversionSpec[T <: InputTransaction]
//     extends FlatSpec
//     with Matchers
//     with ValidatedMatchers
//     with DebugHelpers {
//   this: InputTestData[T] =>

//   s"A VALID input data for ${name}" should "be converted into a valid result" in {
//     val computedV = converter(this.sampleCorrectCsvData).ingestInput
//     computedV shouldBe valid
//   }

//   s"A VALID input data for ${name}" should s"be converted into a sequence of ${name}Transaction" in {
//     val computedV = converter(this.sampleCorrectCsvData).ingestInput
//     val expectedV = this.convertedCorrectData.aValid
//     (computedV, expectedV).mapN { (computed, expected) =>
//       computed should contain theSameElementsInOrderAs (expected)
//     }
//   }

//   s"An INVALID input data for ${name}" should "be converted into an invalid result" in {
//     val computedV = converter(this.sampleBadCsvData).ingestInput
//     computedV shouldBe invalid
//   }

//   s"An INVALID input data for ${name}" should "report correct conversion errors" in {
//     val computedV = converter(this.sampleBadCsvData).ingestInput
//     val computed  = computedV.toEither.left.get.toList
//     computed should contain theSameElementsAs (this.convertedBadData)
//   }

// }
