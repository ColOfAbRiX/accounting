package com.colofabrix.scala.accounting.csv

import cats.implicits._
import com.colofabrix.scala.accounting.csv.CsvDefinitions.{CsvRow, CsvStream}
import com.colofabrix.scala.accounting.csv.CsvFieldParser.CsvRowParser
import com.colofabrix.scala.accounting.utils.AValidation._
import shapeless.ops.hlist.RightFolder
import shapeless.{Generic, HList, HNil, Poly2}
import shapeless.UnaryTCConstraint.*->*


/**
  * Represents an object that can convert CSV files into type A
  */
trait CsvConverter[T] {

  /** Converts a Csv row into a BankTransaction */
  def filterFile(file: CsvStream): AValidated[CsvStream]

  /** Converts a Csv row */
  def convertRow(row: CsvRow): AValidated[T]

  // -- The following has been adapted from https://stackoverflow.com/a/25316124 -- //

  // The "trick" here is to pass the row as the initial value of the fold and carry it along
  // during the computation. Inside the computation we apply a parser using row as parameter and
  // then we append it to the accumulator.

  private type Accumulator[A <: HList] = (CsvRow, AValidated[A])

  private object ApplyRow extends Poly2 {
    implicit def folder[T, V <: HList] = at[CsvRowParser[T], Accumulator[V]] {
      case (rowParser, (row, accumulator)) =>
        val parsed = rowParser(row)
        val next = (accumulator, parsed).mapN((v, t) => t :: v)
        (row, next)
    }
  }

  // UnaryTCConstraint taken from here: https://mpilquist.github.io/blog/2013/06/09/scodec-part-3/

  protected def convert[
    HParsers <: HList : *->*[CsvRowParser]#λ,
    HParsed <: HList](
      row: CsvRow)(
      parsers: HParsers)(
        implicit
        folder: RightFolder.Aux[HParsers, Accumulator[HNil], ApplyRow.type, Accumulator[HParsed]],
        gen: Generic.Aux[T, HParsed]
  ): AValidated[T] = {
    parsers
      .foldRight((row, (HNil: HNil).aValid))(ApplyRow)._2
      .map(gen.from)
  }
}
