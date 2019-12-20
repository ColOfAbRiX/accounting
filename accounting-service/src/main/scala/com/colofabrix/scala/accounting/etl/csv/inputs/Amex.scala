package com.colofabrix.scala.accounting.etl.csv.inputs

import java.time.LocalDate
import com.colofabrix.scala.accounting.etl.csv._
import com.colofabrix.scala.accounting.etl.FieldConverter._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.model.AmexTransaction
import com.colofabrix.scala.accounting.utils.AValidation._
import shapeless._
import com.colofabrix.scala.accounting.etl.RecordConverter

/**
 * Amex CSV file processor
 */
class AmexCsvProcessor extends CsvProcessor[AmexTransaction] with RecordConverter[AmexTransaction] {

  /** Converts a Csv row into a BankTransaction */
  def filterFile(file: RawInput): RawInput = {
    file
      .filter(
        _.filter(_.nonEmpty).nonEmpty
      )
  }

  /** Converts a Csv row into a BankTransaction */
  def convertRow(row: RawRecord): AValidated[AmexTransaction] = {
    convert(row) {
      val date        = parse[LocalDate](r => r(0))("dd/MM/yyyy")
      val reference   = parse[String](r => r(1))
      val amount      = parse[BigDecimal](r => r(2)).map(amount => -1.0 * amount)
      val description = parse[String](r => r(3))
      val extra       = parse[String](r => r(4))

      date :: reference :: amount :: description :: extra :: HNil
    }
  }

}
