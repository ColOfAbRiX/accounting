package com.colofabrix.scala.accounting.etl

import cats.effect._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl._
import com.colofabrix.scala.accounting.etl.csv._
import com.colofabrix.scala.accounting.etl.csv.inputs._
import java.io.File

/**
 * Converts an input source into transactions
 */
trait Loader[T <: InputTransaction] {
  type PipeConvert = fs2.Pipe[IO, AValidated[RawRecord], AValidated[T]]

  /** Converts a stream of validated RawRecords into a stream of validated transactions */
  def load: PipeConvert
}

object Loader {
  /** Converts a given stream into transactions */
  def fromStream[T <: InputTransaction](stream: VRawInput[IO])(implicit converter: Loader[T]) = {
    stream.through(converter.load)
  }

  /** Converts a given InputReader into transactions */
  def fromReader[T <: InputTransaction](reader: InputReader)(implicit converter: Loader[T]) = {
    fromStream(reader.read)
  }

  /** Converts a given File interpreted as CSV into transactions */
  def fromCsvFile[T <: InputTransaction](file: File)(implicit converter: Loader[T]) = {
    fromReader[T](new FileCsvReader(file))
  }

  /** Converts a given path interpreted as CSV file into transactions */
  def fromCsvPath[T <: InputTransaction](path: String)(implicit converter: Loader[T]) = {
    fromCsvFile[T](new File(path))
  }

  implicit val barclaysConverter = new Loader[BarclaysTransaction] {
    def load: PipeConvert = new BarclaysCsvProcessor().process
  }

  implicit val halifaxConverter = new Loader[HalifaxTransaction] {
    def load: PipeConvert = new HalifaxCsvProcessor().process
  }

  implicit val starlingConverter = new Loader[StarlingTransaction] {
    def load: PipeConvert = new StarlingCsvProcessor().process
  }

  implicit val amexConverter = new Loader[AmexTransaction] {
    def load: PipeConvert = new AmexCsvProcessor().process
  }
}
