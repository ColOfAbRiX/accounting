package com.colofabrix.scala.accounting.etl.pipeline

import cats.data._
import cats.effect.IO
import cats.implicits._
import com.colofabrix.scala.accounting.etl._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.inputs._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._
import java.io.File
import InputProcessor._
import Normalizer._
import Cleaner._

object Pipeline {

  /** Converts a given stream into transactions */
  def fromStream[T <: InputTransaction: InputProcessor: Cleaner: Normalizer](
      stream: VRawInput[IO],
  ): VStream[IO, Transaction] = {
    stream
      .through(InputProcessor[T])
      .through(Cleaner[T])
      .through(Normalizer[T])
  }

  /** Converts a given InputReader into transactions */
  def fromReader[T <: InputTransaction: InputProcessor: Cleaner: Normalizer](
      reader: InputReader,
  ): VStream[IO, Transaction] = {
    reader
      .read
      .through(InputProcessor[T])
      .through(Cleaner[T])
      .through(Normalizer[T])
  }

  /** Converts a given File interpreted as CSV into transactions */
  def fromCsvFile[T <: InputTransaction: InputProcessor: Cleaner: Normalizer](
      file: File,
  ): VStream[IO, Transaction] = {
    fromReader[T](new CsvFileReader(file))
  }

  /** Converts a given path interpreted as CSV file into transactions */
  def fromCsvPath[T <: InputTransaction: InputProcessor: Cleaner: Normalizer](
      path: String,
  ): VStream[IO, Transaction] = {
    fromCsvFile[T](new File(path))
  }
}
