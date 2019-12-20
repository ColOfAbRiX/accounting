package com.colofabrix.scala.accounting.etl.csv.inputs

import java.time.LocalDate
import com.colofabrix.scala.accounting.etl.csv._
import com.colofabrix.scala.accounting.etl.FieldConverter._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.model.HalifaxTransaction
import com.colofabrix.scala.accounting.utils.AValidation._
import shapeless._
import com.colofabrix.scala.accounting.etl.RecordConverter

/**
 * Halifax CSV file processor
 */
class HalifaxCsvProcessor extends CsvProcessor[HalifaxTransaction] with RecordConverter[HalifaxTransaction] {

  /** Converts a Csv row into a BankTransaction */
  def filterFile(file: RawInput): RawInput = {
    file
      .drop(1)
      .filter(_.filter(x => x.trim.nonEmpty).nonEmpty)
  }

  /** Converts a Csv row */
  def convertRow(row: RawRecord): AValidated[HalifaxTransaction] = {
    convert(row) {
      val date        = parse[LocalDate](r => r(0))("dd/MM/yyyy")
      val dateEntered = parse[LocalDate](r => r(1))("dd/MM/yyyy")
      val reference   = parse[String](r => r(2))
      val description = parse[String](r => r(3))
      val amount      = parse[BigDecimal](r => r(4)).map(amount => -1.0 * amount)

      date :: dateEntered :: reference :: description :: amount :: HNil
    }
  }

}
