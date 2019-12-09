package com.colofabrix.scala.accounting.csv

import cats.implicits._
import com.colofabrix.scala.accounting.csv.CsvDefinitions.{CsvRow, CsvStream, CsvValidated}
import com.colofabrix.scala.accounting.csv.CsvFieldParser.CsvRowParser
import shapeless.ops.function.FnToProduct
import shapeless.ops.hlist.RightFolder
import shapeless.{HList, HNil, Poly2}
import shapeless.UnaryTCConstraint.*->*


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
    implicit def folder[T, V <: HList] = at[CsvRowParser[T], (CsvRow, CsvValidated[V])] {
      case (rowParser, (row, accumulator)) =>
        val parsed = rowParser(row)
        val next = (accumulator, parsed).mapN((v, t) => t :: v)
        (row, next)
    }
  }

  // UnaryTCConstraint taken from here: https://mpilquist.github.io/blog/2013/06/09/scodec-part-3/

  def convert[
    HParsers <: HList : *->*[CsvRowParser]#λ,
    HParsed <: HList,
    Factory,
    Output](
      parsers: HParsers,
      row: CsvRow,
      factory: Factory)(
      implicit
      folder: RightFolder.Aux[
        HParsers,
        (CsvRow, CsvValidated[HNil]),
        ApplyRow.type,
        (CsvRow, CsvValidated[HParsed])],
      f2p: FnToProduct.Aux[
        Factory,
        HParsed => Output]
  ): CsvValidated[Output] = {
    // HNil: HNil taken from https://stackoverflow.com/a/33304048
    val hlFactory = f2p(factory)
    val seed      = (row, (HNil: HNil).validNec[Throwable])
    val parsed    = parsers.foldRight(seed)(ApplyRow)._2
    val result    = parsed.map(hlFactory)
    result
  }
}
