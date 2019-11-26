package com.colofabrix.scala.accounting.banks

import java.time.LocalDate
import cats.implicits._
import com.colofabrix.scala.accounting.csv.CsvDefinitions._
import com.colofabrix.scala.accounting.csv.CsvRawTypeParser._
import shapeless._
import shapeless.syntax.std.tuple._


object Barclays {

  /**
   * Transaction on a Barclays CSV file
   */
  final case class BarclaysTransaction(
    number: Int,
    date: LocalDate,
    account: String,
    amount: BigDecimal,
    subcategory: String,
    memo: String
  ) extends BankTransaction


  /**
   * Barclays Csv File Worker
   */
  object BarclaysCsvFile extends CsvConverter[BarclaysTransaction] {
    /** Converts a Csv row into a BankTransaction */
    def filterFile(file: CsvStream): CsvValidated[CsvStream] = {
      file.drop(1).validNec
    }

    /** Converts a Csv row into a BankTransaction */
    def convertRow(row: CsvRow): CsvValidated[BarclaysTransaction] = {
      val parsers = (
        parse[Int]       (r => r(0)),
        parse[LocalDate] (r => r(1))("dd/MM/yyyy"),
        parse[String]    (r => r(2)),
        parse[BigDecimal](r => r(3)),
        parse[String]    (r => r(4)),
        parse[String]    (r => r(5))
      )

      object applyRow extends Poly1 {
        implicit def aDefault[A] = at[CsvRowParser[A]](f => f(row))
      }

      parsers.map(applyRow).mapN(BarclaysTransaction)
    }
  }

}