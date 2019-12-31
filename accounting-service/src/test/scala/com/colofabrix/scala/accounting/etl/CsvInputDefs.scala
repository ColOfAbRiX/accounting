package com.colofabrix.scala.accounting.etl

import cats.data._
import cats.data.Validated.{ Invalid, Valid }
import cats.effect._
import cats.implicits._
import cats.scalatest._
import com.colofabrix.scala.accounting.etl.csv._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.DebugHelpers
import com.colofabrix.scala.accounting.utils.validation._
import java.time.LocalDate
import org.scalatest._

/**
 * Defines a mixin to provide test data
 */
trait InputTestData[T <: InputTransaction] {
  def date(y: Int, m: Int, d: Int): LocalDate    = LocalDate.of(y, m, d)
  def name: String                               = this.getClass.getSimpleName.replaceAll("""InputConversion.*$""", "")
  def read(data: List[RawRecord]): VRawInput[IO] = new IterableCsvReader(data).read

  /** Test dataset of correct CSV data */
  def sampleCorrectCsvData: List[RawRecord]
  /** Expected result for conversion of sampleCorrectCsvData */
  def convertedCorrectData: List[T]
  /** Test dataset of invalid CSV data */
  def sampleBadCsvData: List[RawRecord]
  /** Expected result for conversion of sampleBadCsvData */
  def convertedBadData: List[Invalid[NonEmptyChain[String]]]
  /** Data that the processor will drop */
  def sampleDroppedCsvData: List[RawRecord]
}

/**
 * Defines the tests for all input conversions
 */
trait InputConversionSpec[T <: InputTransaction]
    extends FlatSpec
    with Matchers
    with ValidatedMatchers
    with DebugHelpers { this: InputTestData[T] =>

  /** Needed to provide the processor to convert the data */
  implicit val csvProcessor: CsvProcessor[T]

  s"A valid input data for ${name}" should "be converted into a valid result" in {
    val result = read(this.sampleCorrectCsvData).through(csvProcessor.process)
    withValidatedIoStream(result) { computed =>
      computed.foreach(_ shouldBe valid)
    }
  }

  s"A valid input data for ${name}" should s"be converted into a sequence of ${name}Transaction" in {
    val result    = read(this.sampleCorrectCsvData).through(csvProcessor.process)
    val expectedV = this.convertedCorrectData.aValid
    withValidatedIoStream(result) { computedV =>
      (computedV.sequence, expectedV).mapN { (computed, expected) =>
        computed should contain theSameElementsInOrderAs (expected)
      }
    }
  }

  s"An invalid input data for ${name}" should "be converted into an invalid result" in {
    val result = read(this.sampleBadCsvData).through(csvProcessor.process)
    withValidatedIoStream(result) { computed =>
      computed.foreach(_ shouldBe invalid)
    }
  }

  s"An invalid input data for ${name}" should "report correct conversion errors" in {
    val result = read(this.sampleBadCsvData).through(csvProcessor.process)
    withValidatedIoStream(result) { computed =>
      // computed.foreach(println)
      computed should contain theSameElementsAs (this.convertedBadData)
    }
  }

  s"Specific record values for ${name}" should "be dropped from the stream" in {
    val result = read(this.sampleDroppedCsvData).through(csvProcessor.process)
    withValidatedIoStream(result) { computed =>
      computed should have size (0)
    }
  }

}
