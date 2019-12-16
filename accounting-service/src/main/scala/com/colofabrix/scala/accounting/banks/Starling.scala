package com.colofabrix.scala.accounting.banks

import java.time.LocalDate
import com.colofabrix.scala.accounting.csv.CsvConverter
import com.colofabrix.scala.accounting.csv.CsvDefinitions._
import com.colofabrix.scala.accounting.csv.CsvFieldParser._
import com.colofabrix.scala.accounting.model.StarlingTransaction
import com.colofabrix.scala.accounting.utils.AValidation._
import shapeless._
import shapeless.syntax.std.tuple._


object Starling {

  /**
   * Starling Csv File Worker
   */
  object StarlingCsvFile extends CsvConverter[StarlingTransaction] {
    /** Converts a Csv row into a BankTransaction */
    def filterFile(file: CsvFile): AValidated[CsvFile] = {
      file
        .drop(1)
        .filter { row =>
          row.filter(x => x.trim.nonEmpty).nonEmpty &&
          row(1).toLowerCase != "opening balance"
        }
        .aValid
    }

    /** Converts a Csv row into a BankTransaction */
    def convertRow(row: CsvRow): AValidated[StarlingTransaction] = {
      convert(row) {
        val date         = parse[LocalDate] (r => r(0))("dd/MM/yyyy")
        val counterParty = parse[String]    (r => r(1))
        val reference    = parse[String]    (r => r(2))
        val `type`       = parse[String]    (r => r(3))
        val amount       = parse[BigDecimal](r => r(4))
        val balance      = parse[BigDecimal](r => r(5))

        date :: counterParty :: reference :: `type` :: amount :: balance :: HNil
      }
    }
  }

  implicit val starlingCsvConverter: CsvConverter[StarlingTransaction] = StarlingCsvFile

}
