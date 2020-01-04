package com.colofabrix.scala.accounting.etl.inputs

import com.colofabrix.scala.accounting.etl.csv._
import com.colofabrix.scala.accounting.etl.csv.CsvProcessorUtils._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.FieldConverter._
import com.colofabrix.scala.accounting.etl.FieldConverterUtils._
import com.colofabrix.scala.accounting.etl.pipeline._
import com.colofabrix.scala.accounting.etl.RecordConverter
import com.colofabrix.scala.accounting.model.StarlingTransaction
import com.colofabrix.scala.accounting.model.Transaction
import com.colofabrix.scala.accounting.utils.validation._
import java.time.LocalDate
import shapeless._

/**
 * Starling CSV file processor
 */
class StarlingInputProcessor
    extends CsvProcessor[StarlingTransaction]
    with RecordConverter[StarlingTransaction]
    with Cleaner[StarlingTransaction]
    with Transformer[StarlingTransaction] {

  protected def filter: RawInputFilter = {
    dropHeader andThen dropEmptyRows andThen dropAnyMatch(_.toLowerCase.contains("opening balance"))
  }

  protected def convert(record: RawRecord): AValidated[StarlingTransaction] = {
    convertRecord(record) {
      val date         = sParse[LocalDate](r => r(0))("dd/MM/yyyy")
      val counterParty = sParse[String](r => r(1))
      val reference    = sParse[String](r => r(2))
      val `type`       = sParse[String](r => r(3))
      val amount       = sParse[BigDecimal](r => r(4))
      val balance      = sParse[BigDecimal](r => r(5))
      date :: counterParty :: reference :: `type` :: amount :: balance :: HNil
    }
  }

  def clean(tr: StarlingTransaction): StarlingTransaction = {
    val stringCleaning = trim andThen toLowercase andThen removeRedundantSpaces andThen removePunctuation
    tr.copy(
      counterParty = stringCleaning(tr.counterParty),
      reference = stringCleaning(tr.reference),
      `type` = stringCleaning(tr.`type`),
    )
  }

  def transform(input: StarlingTransaction): Transaction = {
    Transaction(input.date, input.amount, input.reference, "Halifax", "", "", "")
  }

}
