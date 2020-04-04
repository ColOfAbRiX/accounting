package com.colofabrix.scala.accounting.etl.pipeline

import cats.effect._
import cats.implicits._
import cats.scalatest._
import com.colofabrix.scala.accounting.etl.definitions._
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
trait PipelineSpecsDefs[T <: InputTransaction]
    extends AnyWordSpec
    with Matchers
    with ValidatedMatchers
    with StreamHelpers { this: InputTestData[T] =>

  /** Needed to provide the processor to convert the data */
  implicit val processor: InputProcessor[T]
  implicit val cleaner: Cleaner[T]

  def runTestPipeline(data: List[RawRecord]): VStream[IO, T] = {
    IterableReader[IO](data)
      .read
      .through(InputProcessor[IO, T])
      .through(Cleaner[IO, T])
  }

  s"The ${name} processor" when {
    "provied with a valid input" should {
      "return a valid result" in {
        val result = runTestPipeline(this.sampleCorrectCsvData)
        withValidatedStream(result) { computed =>
          computed.foreach(_ shouldBe valid)
        }
      }
      s"convert the input into List[${name}InputTransaction]" in {
        val result    = runTestPipeline(this.sampleCorrectCsvData)
        val expectedV = this.convertedCorrectData.aValid
        withValidatedStream(result) { computedV =>
          (computedV.sequence, expectedV).mapN { (computed, expected) =>
            computed should contain theSameElementsInOrderAs (expected)
            ()
          }
          ()
        }
      }
    }

    "provied with an invalid input" should {
      "return an invalid result" in {
        val result = runTestPipeline(this.sampleBadCsvData)
        withValidatedStream(result) { computed =>
          computed.foreach(_ shouldBe invalid)
        }
      }
      "return a detailed description of conversion errors" in {
        val result = runTestPipeline(this.sampleBadCsvData)
        withValidatedStream(result) { computed =>
          computed should contain theSameElementsAs (this.convertedBadData)
          ()
        }
      }
    }

    "provided with specific record values" should {
      "dropped these values" in {
        val result = runTestPipeline(this.sampleDroppedCsvData)
        withValidatedStream(result) { computed =>
          computed should have size (0)
          ()
        }
      }
    }
  }
}
