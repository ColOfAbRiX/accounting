package com.colofabrix.scala.accounting.banks

import java.time.LocalDate
import com.colofabrix.scala.accounting.csv.CsvConverter
import com.colofabrix.scala.accounting.csv.CsvDefinitions._
import com.colofabrix.scala.accounting.csv.CsvFieldParser._
import com.colofabrix.scala.accounting.model.AmexTransaction
import com.colofabrix.scala.accounting.utils.AValidation._
import shapeless._
import shapeless.syntax.std.tuple._

object Amex {

  /**
    * Amex Csv File Worker
    */
  object AmexCsvFile extends CsvConverter[AmexTransaction] {

    /** Converts a Csv row into a BankTransaction */
    protected def filterFile(file: CsvFile): AValidated[CsvFile] =
      file
        .filter(row => row.nonEmpty)
        .aValid

    /** Converts a Csv row into a BankTransaction */
    protected def convertRow(row: CsvRow): AValidated[AmexTransaction] =
      convert(row) {
        val date        = parse[LocalDate](r => r(0))("dd/MM/yyyy")
        val reference   = parse[String](r => r(1))
        val amount      = parse[BigDecimal](r => r(2)).map(amount => -1.0 * amount)
        val description = parse[String](r => r(3))
        val extra       = parse[String](r => r(4))

        date :: reference :: amount :: description :: extra :: HNil
      }
  }

  implicit val amexCsvConverter: CsvConverter[AmexTransaction] = AmexCsvFile

}
