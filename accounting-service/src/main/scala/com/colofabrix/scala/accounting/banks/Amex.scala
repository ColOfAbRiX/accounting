package com.colofabrix.scala.accounting.banks

import java.time.LocalDate
import cats.implicits._
import com.colofabrix.scala.accounting.csv.CsvDefinitions._
import com.colofabrix.scala.accounting.csv.CsvRawTypeParser._
import shapeless._
import shapeless.syntax.std.tuple._


object Amex {

  /**
    * Transaction on a American Express CSV file
    */
  final case class AmexTransaction(
    date: LocalDate,
    reference: String,
    amount: BigDecimal,
    description: String,
    extra: String
  ) extends BankTransaction

  /**
    * Amex Csv File Worker
    */
  object BarclaysCsvFile extends CsvConverter[AmexTransaction] {
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

}
