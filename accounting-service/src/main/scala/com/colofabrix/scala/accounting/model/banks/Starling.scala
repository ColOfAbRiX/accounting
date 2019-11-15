package com.colofabrix.scala.accounting.model.banks

import scala.util._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.model.BankCsvInterpreterUtils._


object Starling {

  final class StarlingCsvFile extends BankFileConverter[StarlingRow] {
    val csvConfig = kantan.csv.rfc

    val dateFormat = "dd/MM/yyyy"

    def adaptFile(file: CsvFile[String]): CsvFile[String] = file.drop(1)

    def convertRow(row: CsvRow[String]): Try[StarlingRow] = {
      implicit val implRow = row

      for {
        date         <- parseLocalDate(0)
        counterParty <- parseString(1)
        reference    <- parseString(2)
        `type`       <- parseString(3)
        amount       <- parseBigDecimal(4)
        balance      <- parseBigDecimal(5)
      } yield {
        StarlingRow(date, counterParty, reference, `type`, amount, balance)
      }
    }
  }

  implicit def starlingConverter: BankFileConverter[StarlingRow] = new StarlingCsvFile()

}
