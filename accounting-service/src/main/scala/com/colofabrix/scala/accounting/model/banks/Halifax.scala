package com.colofabrix.scala.accounting.model.banks

import scala.util._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.model.BankCsvInterpreterUtils._


object Halifax {

  final class HalifaxCsvFile extends BankFileConverter[HalifaxRow] {
    val csvConfig = kantan.csv.rfc

    val dateFormat = "dd/MM/yyyy"

    def adaptFile(file: CsvFile[String]): CsvFile[String] = file.drop(1)

    def convertRow(row: CsvRow[String]): Try[HalifaxRow] = {
      implicit val implRow = row

      for {
        date        <- parseLocalDate(0)
        dateEntered <- parseLocalDate(1)
        reference   <- parseString(2)
        description <- parseString(3)
        amount      <- parseBigDecimal(4)
      } yield {
        HalifaxRow(date, dateEntered, reference, description, amount)
      }
    }
  }

  implicit def halifaxConverter: BankFileConverter[HalifaxRow] = new HalifaxCsvFile()

}
