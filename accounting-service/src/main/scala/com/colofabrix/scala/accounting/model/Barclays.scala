package com.colofabrix.scala.accounting.model

import cats.implicits._
import com.colofabrix.scala.accounting.csv._
import com.colofabrix.scala.accounting.csv.CsvDefinitions._
import java.time.LocalDate
import monix.reactive.Observable
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
  object BarclaysCsvFile extends CsvCleaner[BarclaysTransaction] with CsvConverter[BarclaysTransaction] {
    import CsvRawTypeParser._

    def cleanFile(file: CsvStream): Observable[List[String]] = {
      for {
        row <- file.drop(1)
      } yield {
        row
      }
    }

    private val parsers = (
      parse[Int]        { r: List[String] => r(0) },
      parse[LocalDate]  { r: List[String] => r(1) } ("dd/MM/yyyy"),
      parse[String]     { r: List[String] => r(2) },
      parse[BigDecimal] { r: List[String] => r(3) },
      parse[String]     { r: List[String] => r(4) },
      parse[String]     { r: List[String] => r(5) }
    )

    private def constructor = BarclaysTransaction.apply _

    def convertRow(row: CsvRow): CsvValidated[BarclaysTransaction] = {
      object applyRow extends Poly1 {
        implicit def applyRow[A] = at[CsvRowParser[A]](f => f(row))
      }
      parsers
        .map(applyRow)
        .mapN(constructor)
    }
  }

}
