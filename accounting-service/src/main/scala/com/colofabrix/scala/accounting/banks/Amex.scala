package com.colofabrix.scala.accounting.banks

import java.time.LocalDate
import cats.implicits._
import com.colofabrix.scala.accounting.csv.CsvConverter
import com.colofabrix.scala.accounting.csv.CsvDefinitions._
import com.colofabrix.scala.accounting.csv.CsvFieldParser._
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
      val date        = parse[LocalDate] (r => r(0))("dd/MM/yyyy")
      val reference   = parse[String]    (r => r(1))
      val amount      = parse[BigDecimal](r => r(2)).map(value => -1.0 * value)
      val description = parse[String]    (r => r(3))
      val extra       = parse[String]    (r => r(4))

      val parsers = date :: reference :: amount :: description :: extra :: HNil

      convert(parsers, row, AmexTransaction.apply _)
    }
  }

  implicit val amexCsvConverter: CsvConverter[AmexTransaction] = AmexCsvFile

}
