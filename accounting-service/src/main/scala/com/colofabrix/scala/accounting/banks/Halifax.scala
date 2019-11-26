package com.colofabrix.scala.accounting.banks

import java.time.LocalDate
import cats.implicits._
import com.colofabrix.scala.accounting.csv.CsvDefinitions._
import com.colofabrix.scala.accounting.csv.CsvRawTypeParser._
import shapeless._
import shapeless.syntax.std.tuple._


object Halifax {

  /**
   * Transaction on a Halifax CSV file
   */
  final case class HalifaxTransaction(
    date: LocalDate,
    dateEntered: LocalDate,
    reference: String,
    description: String,
    amount: BigDecimal
  ) extends BankTransaction


  /**
   * Halifax Csv File Worker
   */
  object BarclaysCsvFile extends CsvConverter[HalifaxTransaction] {
    /** Converts a Csv row into a BankTransaction */
    def filterFile(file: CsvStream): CsvValidated[CsvStream] = {
      file.drop(1).validNec
    }

    /** Converts a Csv row into a BankTransaction */
    def convertRow(row: CsvRow): CsvValidated[HalifaxTransaction] = {
      val parsers = (
        parse[LocalDate] (r => r(0))("dd/MM/yyyy"),
        parse[LocalDate] (r => r(1))("dd/MM/yyyy"),
        parse[String]    (r => r(2)),
        parse[String]    (r => r(3)),
        parse[BigDecimal](r => r(4)),
      )

      object applyRow extends Poly1 {
        implicit def aDefault[A] = at[CsvRowParser[A]](f => f(row))
      }

      parsers.map(applyRow).mapN(HalifaxTransaction)
    }
  }

}
