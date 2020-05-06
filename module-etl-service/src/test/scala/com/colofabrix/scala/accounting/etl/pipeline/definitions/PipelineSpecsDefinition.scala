package com.colofabrix.scala.accounting.etl.pipeline.definitions

import cats.scalatest._
import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.utils.PPrintPrettifier
import org.scalatest._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import shapeless._

/**
 * Defines the tests for all input conversions
 */
trait PipelineSpecsDefinition[T <: InputTransaction]
    extends AnyFlatSpec
    with Matchers
    with Inspectors
    with ValidatedMatchers
    with PPrintPrettifier { this: PipelineDefinitions[T] =>

  def runTests(): Unit = {
    object runTest extends Poly1 {
      implicit def caseStep[I, O] = at[PipelineStep[I, O]](runStep)
    }
    pipelineSteps.map(runTest)
    ()
  }

  def runStep[I, O](step: PipelineStep[I, O]): Unit = {
    s"The $name ${step.name} step" should "return Valid() items when provided with a valid input" in {
      val result = step.inputData through step.operation
      forEvery(result) { computed =>
        computed shouldBe valid
      }
    }

    it should "create correct values when provided with a valid input" in {
      val result = unwrapValid(step.inputData through step.operation)
      result should contain theSameElementsInOrderAs step.expectedOutputData
    }

    it should "return Invalid() items when provided with an invalid input" in {
      val result = step.malformedData through step.operation
      forEvery(result) { computed =>
        computed shouldBe invalid
      }
    }

    it should "return a detailed description of the errors" in {
      val result = unwrapInvalid(step.malformedData through step.operation)
      result should have size step.expectedErrorMatches.length.toLong

      forEvery(result zip step.expectedErrorMatches) {
        case (computed, expected) =>
          computed should have size expected.length.toLong

          forEvery(computed zip expected) {
            case (error, regex) =>
              error should include regex regex
          }
      }
    }

    it should "drop specific record values" in {
      val result = unwrapValid(step.removedData through step.operation)
      result should have size 0
    }
  }
}
