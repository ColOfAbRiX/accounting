package com.colofabrix.scala.accounting.etl.inputs

import com.colofabrix.scala.accounting.etl._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.conversion.FieldConverter._
import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.etl.pipeline._
import com.colofabrix.scala.accounting.etl.pipeline.CleanerUtils._
import com.colofabrix.scala.accounting.etl.pipeline.InputProcessorUtils._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._
import java.time.LocalDate
import shapeless._
import java.{ util => ju }

/**
 * Amex API data processor
 */
class AmexApiInput
    extends InputProcessor[AmexTransaction]
    with Cleaner[AmexTransaction]
    with Normalizer[AmexTransaction] {

  def filterInput: RawInputFilter = identity

  def convertRaw(record: RawRecord): AValidated[AmexTransaction] = {
    val converter = new RecordConverter[AmexTransaction] {}
    converter.convertRecord(record) {
      val date        = sParse[LocalDate](r => r(0))("dd/MM/yyyy")
      val reference   = sParse[String](r => r(1))
      val amount      = sParse[BigDecimal](r => r(2)).map(amount => -1.0 * amount)
      val description = sParse[String](r => r(3))
      val extra       = sParse[String](r => r(4))
      date :: reference :: amount :: description :: extra :: HNil
    }
  }

  def cleanInputTransaction(transactions: AmexTransaction): AmexTransaction = {
    val cleaned = Generic[AmexTransaction]
      .to(transactions)
      .map(defaultCleaner)
    Generic[AmexTransaction].from(cleaned)
  }

  def toTransaction(input: AmexTransaction): Transaction = {
    Transaction(ju.UUID.randomUUID, input.date, input.amount, input.description, "Amex", "", "", "")
  }

}

/**
 * Amex CSV data processor
 */
class AmexCsvInput extends AmexApiInput {
  override def filterInput: RawInputFilter = dropEmptyRows
}
