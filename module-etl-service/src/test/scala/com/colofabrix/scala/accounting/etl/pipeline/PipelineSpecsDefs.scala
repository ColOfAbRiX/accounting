package com.colofabrix.scala.accounting.etl.pipeline

import cats.effect._
import cats.implicits._
import cats.scalatest._
import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.etl.pipeline._
import com.colofabrix.scala.accounting.etl.readers._
import com.colofabrix.scala.accounting.utils.StreamHelpers
import com.colofabrix.scala.accounting.utils.validation._
import com.colofabrix.scala.accounting.utils.validation.streams._
import org.scalatest._
import org.scalatest.matchers.should._
import org.scalatest.wordspec._

/**
 * Defines the tests for all input conversions
 */
@SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
trait PipelineSpecsDefs[T <: InputTransaction]
    extends AnyWordSpec
    with Matchers
    with Inspectors
    with ValidatedMatchers
    with StreamHelpers { this: PipelineTestData[T] =>

  /** Needed to provide the processor to convert the data */
  implicit val processor: InputProcessor[T]
  implicit val cleaner: Cleaner[T]

  def processAndClean(data: List[RawRecord]): VStream[IO, T] = {
    IterableReader[IO](data)
      .read
      .through(InputProcessor[IO, T])
      .through(Cleaner[IO, T])
  }

  s"The ${name} processor" when {
    "provided with a valid input" should {
      "return a valid result" in {
        val result = processAndClean(this.sampleCorrectCsvData).compiled
        forAll(result) { computed =>
          computed shouldBe valid
        }
      }
      s"convert the input into List[${name}Transaction]" in {
        val result   = processAndClean(this.sampleCorrectCsvData).compiled.sequence
        val expected = this.convertedCorrectData.aValid
        (result, expected).mapN { (computed, expected) =>
          computed should contain theSameElementsInOrderAs expected
        }
      }
    }

    "provided with an invalid input" should {
      "return an invalid result" in {
        val result = processAndClean(this.sampleBadCsvData).compiled
        forAll(result) { computed =>
          computed shouldBe invalid
        }
      }
      "return a detailed description of conversion errors" in {
        val computed = processAndClean(this.sampleBadCsvData).compiled
        computed should contain theSameElementsAs this.convertedBadData
      }
    }

    "provided with specific record values" should {
      "dropped these values" in {
        val computed = processAndClean(this.sampleDroppedCsvData).compiled
        computed should have size 0
      }
    }
  }
}
