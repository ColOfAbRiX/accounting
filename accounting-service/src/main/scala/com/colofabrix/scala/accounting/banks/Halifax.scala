package com.colofabrix.scala.accounting.banks

import java.time.LocalDate
import cats.implicits._
import com.colofabrix.scala.accounting.csv.CsvDefinitions._
import com.colofabrix.scala.accounting.csv.CsvTypeParser._
import com.colofabrix.scala.accounting.model.HalifaxTransaction
import shapeless._
import shapeless.syntax.std.tuple._


object Halifax {

  /**
   * Halifax Csv File Worker
   */
  object HalifaxCsvFile extends CsvConverter[HalifaxTransaction] {
    /** Converts a Csv row into a BankTransaction */
    def filterFile(file: CsvStream): CsvValidated[CsvStream] = {
      file.drop(1).validNec
    }

    val parsers =
      parse[LocalDate] (r => r(0))("dd/MM/yyyy") ::
      parse[LocalDate] (r => r(1))("dd/MM/yyyy") ::
      parse[String]    (r => r(2)) ::
      parse[String]    (r => r(3)) ::
      parse[BigDecimal](r => r(4)).map(value => -1.0 * value) ::
      HNil

    /** Converts a Csv row */
    def convertRow(row: CsvRow): CsvValidated[HalifaxTransaction] = {
      convertRowGeneric(parsers, row).tupled.mapN(HalifaxTransaction)
    }
  }

  implicit val halifaxCsvConverter: CsvConverter[HalifaxTransaction] = HalifaxCsvFile
}
