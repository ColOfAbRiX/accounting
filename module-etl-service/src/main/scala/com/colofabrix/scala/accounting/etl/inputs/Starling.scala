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
import java.{ util => jutils }

/**
 * Starling API data processor
 */
class StarlingApiInput
    extends InputProcessor[StarlingTransaction]
    with Cleaner[StarlingTransaction]
    with Normalizer[StarlingTransaction] {

  def filterInput: RawInputFilter = identity

  def convertRaw(record: RawRecord): AValidated[StarlingTransaction] = {
    val converter = new RecordConverter[StarlingTransaction] {}
    converter.convertRecord(record) {
      val date         = sParse[LocalDate](r => r(0))("dd/MM/yyyy")
      val counterParty = sParse[String](r => r(1))
      val reference    = sParse[String](r => r(2))
      val `type`       = sParse[String](r => r(3))
      val amount       = sParse[BigDecimal](r => r(4))
      val balance      = sParse[BigDecimal](r => r(5))
      date :: counterParty :: reference :: `type` :: amount :: balance :: HNil
    }
  }

  def cleanInputTransaction(transactions: StarlingTransaction): StarlingTransaction = {
    val cleaned = Generic[StarlingTransaction]
      .to(transactions)
      .map(defaultCleaner)
    Generic[StarlingTransaction].from(cleaned)
  }

  def toTransaction(input: StarlingTransaction): Transaction = {
    Transaction(jutils.UUID.randomUUID, input.date, input.amount, input.reference, "Starling", "", "", "")
  }

}

/**
 * Starling CSV data processor
 */
class StarlingCsvInput extends StarlingApiInput {
  override def filterInput: RawInputFilter = {
    dropHeader andThen dropEmptyRows andThen dropAnyMatch(_.toLowerCase.contains("opening balance"))
  }
}
