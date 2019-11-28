package com.colofabrix.scala.accounting.banks

import java.time.LocalDate
import scala.runtime.Nothing$
import cats.data.Kleisli
import cats.implicits._
import com.colofabrix.scala.accounting.csv.CsvDefinitions._
import com.colofabrix.scala.accounting.csv.CsvTypeParser._
import com.colofabrix.scala.accounting.model.HalifaxTransaction
import shapeless._
import shapeless.syntax.std.tuple._
import syntax.std.traversable._


object Halifax {

  /**
   * Halifax Csv File Worker
   */
  object HalifaxCsvFile extends CsvConverter[HalifaxTransaction] {
    /** Converts a Csv row into a BankTransaction */
    def filterFile(file: CsvStream): CsvValidated[CsvStream] = {
      file.drop(1).validNec
    }

    /** Converts a Csv row into a BankTransaction */
    def convertRow(row: CsvRow): CsvValidated[HalifaxTransaction] = {
      val parsers =
        parse[LocalDate]  (r => r(0))("dd/MM/yyyy") ::
        parse[LocalDate]  (r => r(1))("dd/MM/yyyy") ::
        parse[String]     (r => r(2)) ::
        parse[String]     (r => r(3)) ::
        (parse[BigDecimal](r => r(4)) map (value => -1.0 * value)) ::
        HNil

      object applyRow extends Poly1 {
        implicit def aDefault[A] = at[CsvRowParser[A]](f => f(row))
      }

      parsers.tupled.map(applyRow).mapN(HalifaxTransaction)
    }

    implicit val hnilConvert: CsvRowParser[HNil.type] = Kleisli { _ => HNil.validNec} }

    implicit def hlistConvert[H, T <: HList](
        implicit
        hParser: Lazy[CsvRowParser[H]],
        tParser: Lazy[CsvRowParser[T]]
    ): CsvRowParser[H] :: T = {
      Kleisli { rowValues => hParser.value(rowValues) }

      ???
    }
}

  implicit val halifaxCsvConverter: CsvConverter[HalifaxTransaction] = HalifaxCsvFile

}
