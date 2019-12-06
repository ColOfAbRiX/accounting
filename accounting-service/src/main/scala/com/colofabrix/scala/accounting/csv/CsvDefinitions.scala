package com.colofabrix.scala.accounting.csv

import cats.data._
import com.colofabrix.scala.accounting.csv.CsvTypeParser.CsvRowParser
import java.io.File
import monix.reactive.Observable
import shapeless._
import shapeless.ops.hlist.RightFolder
import shapeless.syntax.std.tuple._
import shapeless.UnaryTCConstraint._


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

    private object ApplyRow extends Poly2 {
      implicit def aDefault[T, V <: HList] = at[CsvRowParser[T], (CsvRow, V)] {
        case (rowParser, (row, accumulator)) => (row, rowParser(row) :: accumulator)
      }
    }

    // Type constraint taken here: https://mpilquist.github.io/blog/2013/06/09/scodec-part-3/

    def convertRowGeneric[
      HP <: HList : *->*[CsvRowParser]#λ,
      HV <: HList](
        input: HP,
        row: CsvRow)(
          implicit
          folder: RightFolder.Aux[HP, (CsvRow, HNil), ApplyRow.type, (CsvRow, HV)]
    ): HV = {
      // HNil: HNil taken from https://stackoverflow.com/a/33304048
      input.foldRight((row, HNil: HNil))(ApplyRow)._2
    }

  }
}
