package com.colofabrix.scala.accounting.banks

import java.time.LocalDate
import cats.implicits._
import com.colofabrix.scala.accounting.csv.CsvDefinitions._
import com.colofabrix.scala.accounting.csv.CsvTypeParser._
import com.colofabrix.scala.accounting.model.AmexTransaction
import shapeless._
import shapeless.syntax.std.tuple._


object Amex {

  /**
    * Amex Csv File Worker
    */
  object AmexCsvFile extends CsvConverter[AmexTransaction] {
    /** Converts a Csv row into a BankTransaction */
    def filterFile(file: CsvStream): CsvValidated[CsvStream] = {
      file.drop(1).validNec
    }

    /** Converts a Csv row into a BankTransaction */
    def convertRow(row: CsvRow): CsvValidated[AmexTransaction] = {
      val parsers = (
        parse[LocalDate] (r => r(0))("dd/MM/yyyy"),
        parse[String]    (r => r(1)),
        parse[BigDecimal](r => r(2)),
        parse[String]    (r => r(3)),
        parse[String]    (r => r(4))
      )

      object applyRow extends Poly1 {
        implicit def aDefault[A] = at[CsvRowParser[A]](f => f(row))
      }

      parsers.map(applyRow).mapN(AmexTransaction)
    }
  }

  implicit val amexCsvConverter: CsvConverter[AmexTransaction] = AmexCsvFile

}
