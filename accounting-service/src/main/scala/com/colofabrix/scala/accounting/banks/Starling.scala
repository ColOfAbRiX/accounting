package com.colofabrix.scala.accounting.banks

import java.time.LocalDate
import cats.implicits._
import com.colofabrix.scala.accounting.csv.CsvConverter
import com.colofabrix.scala.accounting.csv.CsvDefinitions._
import com.colofabrix.scala.accounting.csv.CsvFieldParser._
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
      val date         = parse[LocalDate] (r => r(0))("dd/MM/yyyy")
      val counterParty = parse[String]    (r => r(1))
      val reference    = parse[String]    (r => r(2))
      val `type`       = parse[String]    (r => r(3))
      val amount       = parse[BigDecimal](r => r(4))
      val balance      = parse[BigDecimal](r => r(5))

      val parsers = date :: counterParty :: reference :: `type` :: amount :: balance :: HNil

      convert(parsers, row, StarlingTransaction.apply _)
    }
  }

  implicit val starlingCsvConverter: CsvConverter[StarlingTransaction] = StarlingCsvFile

}
