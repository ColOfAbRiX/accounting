package com.colofabrix.scala.accounting.etl.csv

import java.io.File
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.utils.validation._

/**
 * Interface for a generic CSV reader that reads raw data
 */
trait CsvReader {
  def read: AValidated[RawInput]
}

/**
 * Kantan CSV Reader
 */
class FileCsvReader(file: File) extends CsvReader {
  import kantan.csv._
  import kantan.csv.ops._

  def read: AValidated[RawInput] = TryV {
    file.asUnsafeCsvReader[List[String]](rfc).toList
  }
}

/**
 * Dummy CSV Reader
 */
class DummyCsvReader(input: RawInput) extends CsvReader {
  def read: AValidated[RawInput] = input.aValid
}
