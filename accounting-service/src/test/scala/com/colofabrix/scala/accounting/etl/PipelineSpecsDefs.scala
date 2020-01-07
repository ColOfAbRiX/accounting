package com.colofabrix.scala.accounting.etl

import cats.implicits._
import cats.scalatest._
import com.colofabrix.scala.accounting.etl.pipeline.InputProcessor
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.StreamHelpers
import com.colofabrix.scala.accounting.utils.validation._
import org.scalatest._

/**
 * Defines the tests for all input conversions
 */
trait PipelineSpecsDefs[T <: InputTransaction]
    extends WordSpec
    with Matchers
    with ValidatedMatchers
    with StreamHelpers { this: InputTestData[T] =>

  /** Needed to provide the processor to convert the data */
  implicit val processor: InputProcessor[T]

  s"The ${name} processor" when {
    "provied with a valid input" should {
      "return a valid result" in {
        val result = read(this.sampleCorrectCsvData).through(processor.process)
        withValidatedIoStream(result) { computed =>
          computed.foreach(_ shouldBe valid)
        }
      }
      s"convert the input into List[${name}InputTransaction]" in {
        val result    = read(this.sampleCorrectCsvData).through(processor.process)
        val expectedV = this.convertedCorrectData.aValid
        withValidatedIoStream(result) { computedV =>
          (computedV.sequence, expectedV).mapN { (computed, expected) =>
            computed should contain theSameElementsInOrderAs (expected)
          }
        }
      }
    }

    "provied with an invalid input" should {
      "return an invalid result" in {
        val result = read(this.sampleBadCsvData).through(processor.process)
        withValidatedIoStream(result) { computed =>
          computed.foreach(_ shouldBe invalid)
        }
      }
      "return a detailed description of conversion errors" in {
        val result = read(this.sampleBadCsvData).through(processor.process)
        withValidatedIoStream(result) { computed =>
          computed should contain theSameElementsAs (this.convertedBadData)
        }
      }
    }

    "provided with specific record values" should {
      "dropped these values" in {
        val result = read(this.sampleDroppedCsvData).through(processor.process)
        withValidatedIoStream(result) { computed =>
          computed should have size (0)
        }
      }
    }
  }
}
