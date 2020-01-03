package com.colofabrix.scala.accounting.etl.inputs

import java.time.LocalDate
import com.colofabrix.scala.accounting.etl.csv._
import com.colofabrix.scala.accounting.etl.FieldConverter._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.RecordConverter
import com.colofabrix.scala.accounting.etl.csv.CsvProcessorUtils._
import com.colofabrix.scala.accounting.model.AmexTransaction
import com.colofabrix.scala.accounting.utils.validation._
import shapeless._
import com.colofabrix.scala.accounting.model.Transaction

/**
 * Amex CSV file processor
 */
class AmexCsvProcessor
    extends CsvProcessor[AmexTransaction]
    with RecordConverter[AmexTransaction]
    with Transformer[AmexTransaction] {

  protected def filter: RawInputFilter = dropEmptyRows

  protected def convert(record: RawRecord): AValidated[AmexTransaction] = {
    convertRecord(record) {
      val date        = sParse[LocalDate](r => r(0))("dd/MM/yyyy")
      val reference   = sParse[String](r => r(1))
      val amount      = sParse[BigDecimal](r => r(2)).map(amount => -1.0 * amount)
      val description = sParse[String](r => r(3))
      val extra       = sParse[String](r => r(4))
      date :: reference :: amount :: description :: extra :: HNil
    }
  }

  def transform(input: AmexTransaction): Transaction = {
    Transaction(input.date, input.amount, input.description, "Amex", "", "", "")
  }

}
