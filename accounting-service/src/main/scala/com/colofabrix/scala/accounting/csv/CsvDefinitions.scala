package com.colofabrix.scala.accounting.csv

import cats.data._
import java.io.File
import com.colofabrix.scala.accounting.csv.CsvTypeParser.CsvRowParser
import monix.reactive.Observable
import shapeless._
import shapeless.syntax.std.tuple._
import shapeless.ops.hlist.RightFolder


object CsvDefinitions {

  /** A line of the Csv file, simply a List[String */
  type CsvRow = List[String]
  /** A CsvStream is a steam of CsvRows */
  type CsvStream = Observable[CsvRow]
  /** The type used to validate Csv data */
  type CsvValidated[A] = ValidatedNec[Throwable, A]


  /**
    * Interface for a generic CSV reader that reads raw data
    */
  trait CsvReader {
    def readFile(file: File): CsvValidated[CsvStream]
  }


  /**
    * Type of CSV reader
    */
  sealed trait CsvReaderType

  /** Kantan CSV reader type */
  final case object KantanCsvReaderType extends CsvReaderType

  object CsvReaderType {
    /** Factory method to create a new reader from CsvReaderType */
    def apply(readerType: CsvReaderType): CsvReader = readerType match {
      case KantanCsvReaderType => new KantanCsvReader()
    }
  }


  /**
    * Represents an object that can convert CSV files into type A
    */
  trait CsvConverter[A] {

    /** Converts a Csv row into a BankTransaction */
    def filterFile(file: CsvStream): CsvValidated[CsvStream]

    /** Converts a Csv row */
    def convertRow(row: CsvRow): CsvValidated[A]

    // -- The following has been adapted from https://stackoverflow.com/a/25316124 -- //

    // The "trick" here is to pass the row as the initial value of the fold and carry it along
    // during the computation. Inside the computation we apply a parser using row as parameter and
    // then we append it to the accumulator.

    // Some useful things here too: https://mpilquist.github.io/blog/2013/06/09/scodec-part-3/

    import UnaryTCConstraint._

    private object ApplyRow extends Poly2 {
      implicit def aDefault[T, V <: HList] = at[CsvRowParser[T], (CsvRow, V)] {
        case (rowParser, (row, accumulator)) => (row, rowParser(row) :: accumulator)
      }
    }

    def convertRowGeneric[
      P <: HList : *->*[CsvRowParser]#λ, O <: HList](
      input: P, row: CsvRow)(
      implicit
        parsersFolder: RightFolder[P, (CsvRow, HNil.type), ApplyRow.type]
    ): O = {
      input.foldRight((row, HNil))(ApplyRow)
    }

  }
}
