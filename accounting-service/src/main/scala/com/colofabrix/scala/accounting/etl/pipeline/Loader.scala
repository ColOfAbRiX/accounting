package com.colofabrix.scala.accounting.etl.pipeline

import cats.effect._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl._
import com.colofabrix.scala.accounting.etl.csv._
import com.colofabrix.scala.accounting.etl.inputs._
import java.io.File

/**
 * Converts an input source into transactions
 */
trait Loader[T] {
  /** Converts a stream of validated RawRecords into a stream of validated transactions */
  def load: VPipe[IO, RawRecord, T]
}

object Loader {
  /** Converts a given stream into transactions */
  def fromStream[T](stream: VRawInput[IO])(implicit loader: Loader[T]): VStream[IO, T] = {
    stream.through(loader.load)
  }

  /** Converts a given InputReader into transactions */
  def fromReader[T](reader: InputReader)(implicit loader: Loader[T]): VStream[IO, T] = {
    reader.read.through(loader.load)
  }

  /** Converts a given File interpreted as CSV into transactions */
  def fromCsvFile[T](file: File)(implicit loader: Loader[T]): VStream[IO, T] = {
    fromReader[T](new FileCsvReader(file))
  }

  /** Converts a given path interpreted as CSV file into transactions */
  def fromCsvPath[T](path: String)(implicit loader: Loader[T]): VStream[IO, T] = {
    fromCsvFile[T](new File(path))
  }

  implicit val barclaysConverter: Loader[BarclaysTransaction] =
    new Loader[BarclaysTransaction] {
      def load = new BarclaysCsvProcessor().process
    }

  implicit val halifaxConverter: Loader[HalifaxTransaction] =
    new Loader[HalifaxTransaction] {
      def load = new HalifaxCsvProcessor().process
    }

  implicit val starlingConverter: Loader[StarlingTransaction] =
    new Loader[StarlingTransaction] {
      def load = new StarlingCsvProcessor().process
    }

  implicit val amexConverter: Loader[AmexTransaction] =
    new Loader[AmexTransaction] {
      def load = new AmexCsvProcessor().process
    }
}
