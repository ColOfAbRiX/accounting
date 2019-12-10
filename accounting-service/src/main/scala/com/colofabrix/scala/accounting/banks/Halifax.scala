package com.colofabrix.scala.accounting.banks

import java.time.LocalDate
import cats.implicits._
import com.colofabrix.scala.accounting.csv.CsvConverter
import com.colofabrix.scala.accounting.csv.CsvDefinitions._
import com.colofabrix.scala.accounting.csv.CsvFieldParser._
import com.colofabrix.scala.accounting.model.HalifaxTransaction
import com.colofabrix.scala.accounting.utils.AValidation._
import shapeless._
import shapeless.syntax.std.tuple._


object Halifax {

  /**
   * Halifax Csv File Worker
   */
  object HalifaxCsvFile extends CsvConverter[HalifaxTransaction] {
    /** Converts a Csv row into a BankTransaction */
    def filterFile(file: CsvStream): AValidated[CsvStream] = {
      file.drop(1).validNec
    }

    /** Converts a Csv row */
    def convertRow(row: CsvRow): AValidated[HalifaxTransaction] = {
      convert(row) {
        val date        = parse[LocalDate] (r => r(0))("dd/MM/yyyy")
        val dateEntered = parse[LocalDate] (r => r(1))("dd/MM/yyyy")
        val reference   = parse[String]    (r => r(2))
        val description = parse[String]    (r => r(3))
        val amount      = parse[BigDecimal](r => r(4)).map(value => -1.0 * value)

        date :: dateEntered :: reference :: description :: amount :: HNil
      }
    }
  }

  implicit val halifaxCsvConverter: CsvConverter[HalifaxTransaction] = HalifaxCsvFile
}
