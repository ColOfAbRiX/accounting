package com.colofabrix.scala.accounting.etl.csv

import java.io.File
import scala.util._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.utils.AValidation._

/**
 * Interface for a generic CSV reader that reads raw data
 */
trait CsvReader {
  def read: AValidated[RawInput]
}

/**
 * Kantan CSV Reader
 */
class KantanCsvReader(file: File) extends CsvReader {
  import kantan.csv._
  import kantan.csv.ops._

  def read: AValidated[RawInput] =
    Try {
      file.asUnsafeCsvReader[List[String]](rfc).toList
    }.toAValidated
}

/**
 * Dummy CSV Reader
 */
class DummyCsvReader(input: RawInput) extends CsvReader {
  def read: AValidated[RawInput] = input.aValid
}

// /**
//  * Type of CSV readers
//  */
// sealed trait CsvReaderType
// final case object KantanCsvReaderType extends CsvReaderType

// object CsvReaderType {
//   /** Factory method to create a new reader from CsvReaderType */
//   def apply(readerType: CsvReaderType): CsvReader = readerType match {
//     case KantanCsvReaderType => new KantanCsvReader()
//   }
// }
