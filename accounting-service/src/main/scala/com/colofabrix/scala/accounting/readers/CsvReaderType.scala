package com.colofabrix.scala.accounting.readers


/**
 * Type of CSV reader
 */
sealed trait CsvReaderType

/** Kantan CSV Library */
final case object KantanCsvReaderType extends CsvReaderType

object CsvReaderType {
  /**
   * Factory method to create a new reader from CsvReaderType
   */
  def apply(readerType: CsvReaderType): CsvReader = readerType match {
    case KantanCsvReaderType => new KantanCsvReader()
  }
}
