package com.colofabrix.scala.accounting.etl.inputs

import com.colofabrix.scala.accounting.etl._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.FieldConverter._
import com.colofabrix.scala.accounting.etl.pipeline._
import com.colofabrix.scala.accounting.etl.pipeline.CleanerUtils._
import com.colofabrix.scala.accounting.etl.pipeline.InputProcessorUtils._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._
import java.time.LocalDate
import shapeless._

/**
 * Starling CSV file processor
 */
class StarlingInputProcessor
    extends InputProcessor[StarlingTransaction]
    with Cleaner[StarlingTransaction]
    with Normalizer[StarlingTransaction] {

  protected def filterInput: RawInputFilter = {
    dropHeader andThen dropEmptyRows andThen dropAnyMatch(_.toLowerCase.contains("opening balance"))
  }

  protected def convertRaw(record: RawRecord): AValidated[StarlingTransaction] = {
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

  def clean(transactions: StarlingTransaction): StarlingTransaction = {
    val cleaned = Generic[StarlingTransaction]
      .to(transactions)
      .map(defaultCleaner)
    Generic[StarlingTransaction].from(cleaned)
  }

  def toTransaction(input: StarlingTransaction): Transaction = {
    Transaction(input.date, input.amount, input.reference, InputName("Starling"), "", "", "")
  }

}
