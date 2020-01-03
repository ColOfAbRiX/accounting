package com.colofabrix.scala.accounting.etl.inputs

import java.time.LocalDate
import com.colofabrix.scala.accounting.etl.csv._
import com.colofabrix.scala.accounting.etl.FieldConverter._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.RecordConverter
import com.colofabrix.scala.accounting.etl.csv.CsvProcessorUtils._
import com.colofabrix.scala.accounting.model.HalifaxTransaction
import com.colofabrix.scala.accounting.utils.validation._
import shapeless._
import com.colofabrix.scala.accounting.model.Transaction

/**
 * Halifax CSV file processor
 */
class HalifaxCsvProcessor
    extends CsvProcessor[HalifaxTransaction]
    with RecordConverter[HalifaxTransaction]
    with Transformer[HalifaxTransaction] {

  protected def filter: RawInputFilter = dropHeader andThen dropEmptyRows

  protected def convert(record: RawRecord): AValidated[HalifaxTransaction] = {
    convertRecord(record) {
      val date        = sParse[LocalDate](r => r(0))("dd/MM/yyyy")
      val dateEntered = sParse[LocalDate](r => r(1))("dd/MM/yyyy")
      val reference   = sParse[String](r => r(2))
      val description = sParse[String](r => r(3))
      val amount      = sParse[BigDecimal](r => r(4)).map(amount => -1.0 * amount)
      date :: dateEntered :: reference :: description :: amount :: HNil
    }
  }

  def transform(input: HalifaxTransaction): Transaction = {
    Transaction(input.date, input.amount, input.description, "Halifax", "", "", "")
  }

}
