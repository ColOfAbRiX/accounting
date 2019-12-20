package com.colofabrix.scala.accounting.etl.csv.inputs

import java.time.LocalDate
import com.colofabrix.scala.accounting.etl.csv._
import com.colofabrix.scala.accounting.etl.FieldConverter._
import com.colofabrix.scala.accounting.etl.InputDefinitions._
import com.colofabrix.scala.accounting.model.StarlingTransaction
import com.colofabrix.scala.accounting.utils.AValidation._
import shapeless._
import com.colofabrix.scala.accounting.etl.RecordConverter

/**
 * Starling CSV file processor
 */
class StarlingCsvProcessor extends CsvProcessor[StarlingTransaction] with RecordConverter[StarlingTransaction] {

  /** Converts a Csv row into a BankTransaction */
  def filterFile(file: RawInput): RawInput = {
    file
      .drop(1)
      .filter { row =>
        row.filter(x => x.trim.nonEmpty).nonEmpty &&
        row(1).toLowerCase != "opening balance"
      }
  }

  /** Converts a Csv row into a BankTransaction */
  def convertRow(row: RawRecord): AValidated[StarlingTransaction] = {
    convert(row) {
      val date         = parse[LocalDate](r => r(0))("dd/MM/yyyy")
      val counterParty = parse[String](r => r(1))
      val reference    = parse[String](r => r(2))
      val `type`       = parse[String](r => r(3))
      val amount       = parse[BigDecimal](r => r(4))
      val balance      = parse[BigDecimal](r => r(5))

      date :: counterParty :: reference :: `type` :: amount :: balance :: HNil
    }
  }

}
