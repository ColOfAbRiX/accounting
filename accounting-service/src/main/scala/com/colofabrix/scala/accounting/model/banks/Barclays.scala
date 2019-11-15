package com.colofabrix.scala.accounting.model.banks

import scala.util._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.model.BankCsvInterpreterUtils._


object Barclays {

  final class BarclaysCsvFile extends BankFileConverter[BarclaysRow] {
    val csvConfig = kantan.csv.rfc

    val dateFormat = "dd/MM/yyyy"

    def adaptFile(file: CsvFile[String]): CsvFile[String] = file.drop(1)

    def convertRow(row: CsvRow[String]): Try[BarclaysRow] = {
      implicit val implRow = row

      for {
        number      <- Success(parseInt(0).toOption)
        date        <- parseLocalDate(1)
        account     <- parseString(2)
        amount      <- parseBigDecimal(3)
        subcategory <- parseString(4)
        memo        <- parseString(5)
      } yield {
        BarclaysRow(number, date, account, amount, subcategory, memo)
      }
    }
  }

  implicit def barclaysConverter: BankFileConverter[BarclaysRow] = new BarclaysCsvFile()

}
