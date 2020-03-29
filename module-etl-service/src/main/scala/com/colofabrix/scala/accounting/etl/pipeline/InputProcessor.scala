package com.colofabrix.scala.accounting.etl.pipeline

import cats.data._
import cats.implicits._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.utils.logging._
import com.colofabrix.scala.accounting.utils.validation._
import fs2.Pure

/**
 * Converts an input source into transactions
 */
trait InputProcessor[T <: InputTransaction] {
  /** Converts one raw record into an input transaction */
  def convertRaw(record: RawRecord): AValidated[T]

  /** Filter an input to adapt it for processing, like removing head, empty rows and so on */
  def filterInput: VPipe[Pure, RawRecord, RawRecord]
}

object InputProcessor extends PipeLogging {
  protected[this] val logger = org.log4s.getLogger

  /**
   * Converts a stream of RawRecord into InputTransaction
   */
  def apply[F[_], T <: InputTransaction](implicit ip: InputProcessor[T]): VPipe[Pure, RawRecord, T] = {
    val log: VPipe[Pure, RawRecord, RawRecord]    = pipeLogger.trace(x => s"Processing record: ${x.toString}")
    val filter: VPipe[Pure, RawRecord, RawRecord] = ip.filterInput
    val convert: VPipe[Pure, RawRecord, T]        = _.map(_ andThen ip.convertRaw)

    log andThen filter andThen convert
  }
}

/**
 * Utility functions for CsvInputProcessor
 */
object InputProcessorUtils {

  /** Represents a processor of a raw input */
  type RawInputFilter = VPipe[Pure, RawRecord, RawRecord]

  /** Drops records based on a condition on Valid data */
  private[this] def validDropOnCondition[A](f: RawRecord => Boolean): RawInputFilter = { input =>
    input.filter(vRecord => vRecord.fold(_ => true, !f(_)))
  }

  /** Drops records based on a condition on Valid data */

  /** Applies a function to a field that can be null */
  private[this] def nullSafe[A](default: A, f: String => A): String => A = { field =>
    Option(field).map(f).getOrElse(default)
  }

  /** Drops the header of the input */
  def dropHeader: RawInputFilter = {
    _.drop(1)
  }

  /** Drops records that don't respect a length */
  def dropLength(check: Int => Boolean): RawInputFilter = {
    validDropOnCondition(record => check(record.length))
  }

  /** Drop a row if a match is found anywhere */
  def dropAnyMatch(p: String => Boolean): RawInputFilter = {
    validDropOnCondition { record =>
      record.exists(nullSafe(false, p))
    }
  }

  /** Drops records that are all empty or all null */
  def dropEmptyRows: RawInputFilter = {
    validDropOnCondition { record =>
      record.forall(nullSafe(true, _.trim.isEmpty))
    }
  }

  /** Converts null fields into empty strings */
  def fixNulls: RawInputFilter = { input =>
    Nested(Nested(input))
      .map(nullSafe("", identity))
      .value.value
  }

}
