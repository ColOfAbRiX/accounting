package com.colofabrix.scala.accounting.etl.pipeline.definitions

import java.time.LocalDate
import cats.implicits._
import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._
import com.colofabrix.scala.accounting.utils.validation.streams._
import fs2._
import shapeless._

/**
 * Represents a step in the pipeline
 */
trait PipelineStep[Input, Output] {
  /** Name of the step */
  def name: String

  /** Operation performed by the step */
  def operation: VPipe[Pure, Input, Output]

  /** Valid input data to test */
  def inputData: List[Input]
  /** Expected output of processed valid input */
  def expectedOutputData: List[Output]

  /** Malformed input data to test */
  def malformedData: List[Input]
  /** Expected list of errors to match inside the processed malformed inputs */
  def expectedErrorMatches: List[List[String]]

  /** Data expected to be removed from the output */
  def removedData: List[Input]
}

/**
 * Definition of a pipeline
 */
trait PipelineDefinitions[T <: InputTransaction] {
  implicit class PipelineDataList[I](self: List[I]) {
    def through[O](pipe: VPipe[Pure, I, O]): List[AValidated[O]] =
      Stream
        .emits(self)
        .map(_.valid)
        .through(pipe)
        .toList
  }

  def name: String

  /** Shortcuts for dates */
  def date(y: Int, m: Int, d: Int): LocalDate = LocalDate.of(y, m, d)

  /** Unwrap a list of Valid items and discards the invalid items */
  def unwrapValid[A](data: List[AValidated[A]]): List[A] =
    data.flatMap {
      _.fold(_ => List.empty, List(_))
    }

  /** Unwrap a list of Invalid error descriptions and discards the valid items */
  def unwrapInvalid[A](data: List[AValidated[A]]): List[List[String]] =
    data.flatMap {
      _.fold(x => List(x.toList), _ => List.empty)
    }

  /** The complete pipeline to test as shapeless.HList to preserve types */
  def pipelineSteps: PipelineStep[RawRecord, T] ::
  PipelineStep[T, T] ::
  PipelineStep[T, Transaction] ::
  PipelineStep[RawRecord, Transaction] ::
  HNil
}
