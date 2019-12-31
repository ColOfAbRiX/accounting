package com.colofabrix.scala.accounting.etl.inputs

import java.time.LocalDate
import com.colofabrix.scala.accounting.etl.csv._
import com.colofabrix.scala.accounting.etl.FieldConverter._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.RecordConverter
import com.colofabrix.scala.accounting.etl.csv.CsvProcessorUtils._
import com.colofabrix.scala.accounting.model.StarlingTransaction
import com.colofabrix.scala.accounting.utils.validation._
import shapeless._
import com.colofabrix.scala.accounting.model.Transaction

/**
 * Starling CSV file processor
 */
class StarlingCsvProcessor extends CsvProcessor[StarlingTransaction] with RecordConverter[StarlingTransaction] {

  protected def filter: RawInputFilter = {
    dropHeader andThen dropEmptyRows andThen dropAnyMatch(_.toLowerCase.contains("opening balance"))
  }

  protected def convert(record: RawRecord): AValidated[StarlingTransaction] = {
    convertRecord(record) {
      val date         = parse[LocalDate](r => r(0))("dd/MM/yyyy")
      val counterParty = parse[String](r => r(1))
      val reference    = parse[String](r => r(2))
      val `type`       = parse[String](r => r(3))
      val amount       = parse[BigDecimal](r => r(4))
      val balance      = parse[BigDecimal](r => r(5))
      date :: counterParty :: reference :: `type` :: amount :: balance :: HNil
    }
  }

  def transform(input: StarlingTransaction): Transaction = {
    Transaction(input.date, input.amount, input.reference, "Halifax", "", "", "")
  }

}
