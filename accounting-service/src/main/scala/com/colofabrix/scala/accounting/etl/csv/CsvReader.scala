package com.colofabrix.scala.accounting.etl.csv

import java.io.File
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.utils.validation._
import cats.effect.IO

/**
 * Interface for a generic CSV reader that reads raw data
 */
trait CsvReader {
  def read: IO[AValidated[RawInput]]
}

/**
 * CSV Reader
 */
class CsvFileReader(file: File) extends CsvReader {
  import kantan.csv._
  import kantan.csv.ops._

  def read: IO[AValidated[RawInput]] = TryV {
    val iterator = file.asUnsafeCsvReader[List[String]](rfc)
    fs2.Stream.unfold(iterator) { i =>
      if (i.hasNext) Some((i.next, i)) else None
    }
  }
}

/**
 * Dummy CSV Reader
 */
class DummyCsvReader(input: RawInput) extends CsvReader {
  def read: IO[AValidated[RawInput]] = IO.pure(input.aValid)
}
