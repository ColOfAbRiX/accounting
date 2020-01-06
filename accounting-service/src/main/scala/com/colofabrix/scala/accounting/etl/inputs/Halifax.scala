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
 * Halifax CSV file processor
 */
class HalifaxInputProcessor
    extends InputProcessor[HalifaxTransaction]
    with Cleaner[HalifaxTransaction]
    with Normalizer[HalifaxTransaction] {

  protected def filterInput: RawInputFilter = dropHeader andThen dropEmptyRows

  protected def convertRaw(record: RawRecord): AValidated[HalifaxTransaction] = {
    val converter = new RecordConverter[HalifaxTransaction] {}
    converter.convertRecord(record) {
      val date        = sParse[LocalDate](r => r(0))("dd/MM/yyyy")
      val dateEntered = sParse[LocalDate](r => r(1))("dd/MM/yyyy")
      val reference   = sParse[String](r => r(2))
      val description = sParse[String](r => r(3))
      val amount      = sParse[BigDecimal](r => r(4)).map(amount => -1.0 * amount)
      date :: dateEntered :: reference :: description :: amount :: HNil
    }
  }

  def clean(transactions: HalifaxTransaction): HalifaxTransaction = {
    val cleaned = Generic[HalifaxTransaction]
      .to(transactions)
      .map(defaultCleaner)
    Generic[HalifaxTransaction].from(cleaned)
  }

  def toTransaction(input: HalifaxTransaction): Transaction = {
    Transaction(input.date, input.amount, input.description, InputName("Halifax"), "", "", "")
  }

}
