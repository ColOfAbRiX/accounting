package com.colofabrix.scala.accounting.etl.csv

import cats.implicits._
import com.colofabrix.scala.accounting.etl._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._
import cats.effect.IO
import fs2.Pipe
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.data._
import shapeless.record

/**
 * Processes a CSV file like filtering bad records and converting them to case classes
 */
trait CsvProcessor[+T <: InputTransaction] {
  /** Converts one CSV record into an input transaction */
  protected def convert(record: RawRecord): AValidated[T]

  /** Filter an input to adapt it for processing, like removing head, empty rows and so on */
  protected def filter: VPipe[fs2.Pure, RawRecord, RawRecord]

  /** Processes the input data by filtering and converting the stream */
  def process: VPipe[fs2.Pure, RawRecord, T] = { record =>
    filter(
      record.map{r => /*println(s"Original: $r"); */r}
    ).map { r =>
      // println(s"Record: $r")
      r.flatMapV(convert)
    }
  }
}

/**
 * Utility functions for CsvProcessor
 */
object CsvProcessorUtils {

  /** Represents a processor of a raw input */
  type RawInputFilter = VPipe[fs2.Pure, RawRecord, RawRecord]

  /** Drops records based on a condition on Valid data */
  private def validDropOnCondition[A](f: RawRecord => Boolean): RawInputFilter = { input =>
    input.filter(vRecord => vRecord.fold(_ => true, !f(_)))
  }

  /** Drops records based on a condition on Valid data */
  private def invalidDropOnCondition[A](f: NonEmptyChain[String] => Boolean): RawInputFilter = { input =>
    input.filter(vRecord => vRecord.fold(!f(_), _ => true))
  }

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
    input.map(_.map(_.map(nullSafe("", identity))))
  }

}
