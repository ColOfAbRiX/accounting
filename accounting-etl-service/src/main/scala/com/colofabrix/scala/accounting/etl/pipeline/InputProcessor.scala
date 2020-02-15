package com.colofabrix.scala.accounting.etl.pipeline

import cats.data._
import cats.implicits._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.utils.validation._
import fs2.Pure
import org.log4s._

/**
 * Converts an input source into transactions
 */
trait InputProcessor[T <: InputTransaction] {
  /** Converts one raw record into an input transaction */
  def convertRaw(record: RawRecord): AValidated[T]

  /** Filter an input to adapt it for processing, like removing head, empty rows and so on */
  def filterInput: VPipe[Pure, RawRecord, RawRecord]
}

object InputProcessor {
  private[this] val logger = getLogger

  /** Converts a stream of RawRecord into InputTransaction */
  def apply[T <: InputTransaction](implicit L: InputProcessor[T]): VPipe[Pure, RawRecord, T] = {
    L.filterInput andThen {
      _.map(_ andThen L.convertRaw)
    }
  }
}

/**
 * Utility functions for CsvInputProcessor
 */
object InputProcessorUtils {

  /** Represents a processor of a raw input */
  type RawInputFilter = VPipe[Pure, RawRecord, RawRecord]

  /** Drops records based on a condition on Valid data */
  private def validDropOnCondition[A](f: RawRecord => Boolean): RawInputFilter = { input =>
    input.filter(vRecord => vRecord.fold(_ => true, !f(_)))
  }

  /** Drops records based on a condition on Valid data */

  /** Applies a function to a field that can be null */
  private def nullSafe[A](default: A, f: String => A): String => A = { field =>
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
