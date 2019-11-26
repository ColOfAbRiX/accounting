package com.colofabrix.scala.accounting.banks

import java.time.LocalDate
import cats.implicits._
import com.colofabrix.scala.accounting.csv.CsvDefinitions._
import com.colofabrix.scala.accounting.csv.CsvTypeParser._
import com.colofabrix.scala.accounting.model.StarlingTransaction
import shapeless._
import shapeless.syntax.std.tuple._


object Starling {

  /**
   * Starling Csv File Worker
   */
  object StarlingCsvFile extends CsvConverter[StarlingTransaction] {
    /** Converts a Csv row into a BankTransaction */
    def filterFile(file: CsvStream): CsvValidated[CsvStream] = {
      file.drop(1).validNec
    }

    /** Converts a Csv row into a BankTransaction */
    def convertRow(row: CsvRow): CsvValidated[StarlingTransaction] = {
      val parsers = (
        parse[LocalDate] (r => r(0))("dd/MM/yyyy"),
        parse[String]    (r => r(1)),
        parse[String]    (r => r(2)),
        parse[String]    (r => r(3)),
        parse[BigDecimal](r => r(4)),
        parse[BigDecimal](r => r(5)),
      )

      object applyRow extends Poly1 {
        implicit def aDefault[A] = at[CsvRowParser[A]](f => f(row))
      }

      parsers.map(applyRow).mapN(StarlingTransaction)
    }
  }

  implicit val starlingCsvConverter: CsvConverter[StarlingTransaction] = StarlingCsvFile

}
