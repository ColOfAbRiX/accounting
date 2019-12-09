package com.colofabrix.scala.accounting.banks

import java.time.LocalDate
import cats.implicits._
import com.colofabrix.scala.accounting.csv.CsvConverter
import com.colofabrix.scala.accounting.csv.CsvDefinitions._
import com.colofabrix.scala.accounting.csv.CsvFieldParser._
import com.colofabrix.scala.accounting.model.BarclaysTransaction
import shapeless._
import shapeless.syntax.std.tuple._


object Barclays {

  /**
   * Barclays Csv File Worker
   */
  object BarclaysCsvFile extends CsvConverter[BarclaysTransaction] {
    /** Converts a Csv row into a BankTransaction */
    def filterFile(file: CsvStream): CsvValidated[CsvStream] = {
      file
        .drop(1)
        .filter(row => row.nonEmpty)
        .validNec
    }

    /** Converts a Csv row into a BankTransaction */
    def convertRow(row: CsvRow): CsvValidated[BarclaysTransaction] = {
      val number      = parse[Option[Int]](r => r(0))
      val date        = parse[LocalDate]  (r => r(1))("dd/MM/yyyy")
      val account     = parse[String]     (r => r(2))
      val amount      = parse[BigDecimal] (r => r(3))
      val subcategory = parse[String]     (r => r(4))
      val memo        = parse[String]     (r => r(5))

      val parsers = number :: date :: account :: amount :: subcategory :: memo :: HNil

      convert(parsers, row, BarclaysTransaction.apply _)
    }
  }

  implicit val barclaysCsvConverter: CsvConverter[BarclaysTransaction] = BarclaysCsvFile

}
