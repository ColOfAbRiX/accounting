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
 * Barclays CSV file processor
 */
class BarclaysInputProcessor
    extends InputProcessor[BarclaysTransaction]
    with Cleaner[BarclaysTransaction]
    with Normalizer[BarclaysTransaction] {

  protected def filterInput: RawInputFilter = dropHeader andThen dropEmptyRows

  protected def convertRaw(record: RawRecord): AValidated[BarclaysTransaction] = {
    val converter = new RecordConverter[BarclaysTransaction] {}
    converter.convertRecord(record) {
      val number      = sParse[Option[Int]](r => r(0))
      val date        = sParse[LocalDate](r => r(1))("dd/MM/yyyy")
      val account     = sParse[String](r => r(2))
      val amount      = sParse[BigDecimal](r => r(3))
      val subcategory = sParse[String](r => r(4))
      val memo        = sParse[String](r => r(5))
      number :: date :: account :: amount :: subcategory :: memo :: HNil
    }
  }

  def clean(transactions: BarclaysTransaction): BarclaysTransaction = {
    val cleaned = Generic[BarclaysTransaction]
      .to(transactions)
      .map(defaultCleaner)
    Generic[BarclaysTransaction].from(cleaned)
  }

  def toTransaction(input: BarclaysTransaction): Transaction = {
    Transaction(input.date, input.amount, input.memo, InputName("Barclays"), "", "", "")
  }

}
