package com.colofabrix.scala.accounting.banks

import java.time.LocalDate
import cats.implicits._
import com.colofabrix.scala.accounting.csv.CsvDefinitions._
import com.colofabrix.scala.accounting.csv.CsvTypeParser._
import com.colofabrix.scala.accounting.model.HalifaxTransaction
import shapeless._
import shapeless.ops.hlist.RightFolder
import shapeless.syntax.std.tuple._


object Halifax {

  /**
   * Halifax Csv File Worker
   */
  object HalifaxCsvFile extends CsvConverter[HalifaxTransaction] {
    /** Converts a Csv row into a BankTransaction */
    def filterFile(file: CsvStream): CsvValidated[CsvStream] = {
      file.drop(1).validNec
    }

    val parsers =
      parse[LocalDate](r => r(0))("dd/MM/yyyy") ::
      parse[LocalDate](r => r(1))("dd/MM/yyyy") ::
      parse[String](r => r(2)) ::
      parse[String](r => r(3)) ::
      (parse[BigDecimal](r => r(4)) map (value => -1.0 * value)) ::
      HNil

    /////

    object applyRow2 extends Poly2 {
      implicit def aDefault[A, B <: HList] = at[CsvRowParser[A], (CsvRow, B)] {
        case (rowParser, (row, acc)) =>
          val parsed = rowParser(row)
          (row, parsed :: acc)
      }
    }

    implicit def convertRowGeneric[L <: HList, O <: HList](
        input: L, row: CsvRow)(
        implicit
        folder: RightFolder[L, (CsvRow, HNil.type), applyRow2.type]
    ) = {
      input.foldRight((row, HNil))(applyRow2)
    }

    /** Converts a Csv row into a BankTransaction */
    def convertRow(row: CsvRow): CsvValidated[HalifaxTransaction] = {
      val result = convertRowGeneric(parsers, row)

      object applyRow extends Poly1 {
        implicit def aDefault[A] = at[CsvRowParser[A]](f => f(row))
      }

      parsers.tupled.map(applyRow).mapN(HalifaxTransaction)
    }
  }

  implicit val halifaxCsvConverter: CsvConverter[HalifaxTransaction] = HalifaxCsvFile
}
