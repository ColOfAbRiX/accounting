package com.colofabrix.scala.accounting.model.banks

import scala.util._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.model.BankCsvInterpreterUtils._


object Amex {

  final class AmexCsvFile extends BankFileConverter[AmexRow] {
    val csvConfig = kantan.csv.rfc

    val dateFormat = "dd/MM/yyyy"

    def adaptFile(file: CsvFile[String]): CsvFile[String] = file

    def convertRow(row: CsvRow[String]): Try[AmexRow] = {
      implicit val implRow = row

      for {
        date        <- parseLocalDate(0)
        reference   <- parseString(1)
        amount      <- parseBigDecimal(2)
        description <- parseString(3)
        extra       <- parseString(4)
      } yield {
        AmexRow(date, reference, amount, description, extra)
      }
    }
  }

  implicit def amexConverter: BankFileConverter[AmexRow] = new AmexCsvFile()

}
