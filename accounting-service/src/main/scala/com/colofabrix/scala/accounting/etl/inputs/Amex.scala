package com.colofabrix.scala.accounting.etl.inputs

import com.colofabrix.scala.accounting.etl.csv._
import com.colofabrix.scala.accounting.etl.csv.CsvProcessorUtils._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.FieldConverter._
import com.colofabrix.scala.accounting.etl.FieldConverterUtils._
import com.colofabrix.scala.accounting.etl.pipeline._
import com.colofabrix.scala.accounting.etl.RecordConverter
import com.colofabrix.scala.accounting.model.AmexTransaction
import com.colofabrix.scala.accounting.model.Transaction
import com.colofabrix.scala.accounting.utils.validation._
import java.time.LocalDate
import shapeless._

/**
 * Amex CSV file processor
 */
class AmexInputProcessor
    extends CsvProcessor[AmexTransaction]
    with RecordConverter[AmexTransaction]
    with Cleaner[AmexTransaction]
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

  def clean(tr: AmexTransaction): AmexTransaction = {
    val stringCleaning = toLowercase andThen removeRedundantSpaces
    tr.copy(
      reference = stringCleaning(tr.reference),
      description = stringCleaning(tr.description),
      extra = stringCleaning(tr.extra),
    )
  }

  def transform(input: AmexTransaction): Transaction = {
    Transaction(input.date, input.amount, input.description, "Amex", "", "", "")
  }

}
