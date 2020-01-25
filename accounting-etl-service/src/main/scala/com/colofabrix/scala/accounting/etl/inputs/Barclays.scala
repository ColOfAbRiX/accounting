package com.colofabrix.scala.accounting.etl.inputs

import cats.implicits._
import java.time.LocalDate
import com.colofabrix.scala.accounting.etl._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.FieldConverter._
import com.colofabrix.scala.accounting.etl.pipeline._
import com.colofabrix.scala.accounting.etl.pipeline.CleanerUtils._
import com.colofabrix.scala.accounting.etl.pipeline.InputProcessorUtils._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._
import shapeless._
import com.colofabrix.scala.accounting.model.newtypes.InAmount

/**
 * Barclays API data processor
 */
class BarclaysApiInput
    extends InputProcessor[BarclaysTransaction]
    with Cleaner[BarclaysTransaction]
    with Normalizer[BarclaysTransaction] {

  def filterInput: RawInputFilter = identity

  // implicit val a: FieldConverter[String, InAmount] = newtypeParser[BigDecimal, InAmount]
  // implicit val b = implicitly[FieldConverter[String, InAmount]]

  def convertRaw(record: RawRecord): AValidated[BarclaysTransaction] = {
    val converter = new RecordConverter[BarclaysTransaction] {}
    converter.convertRecord(record) {
      val number      = sParse[Option[Int]](r => r(0))
      val date        = sParse[LocalDate](r => r(1))("dd/MM/yyyy")
      val account     = sParse[String](r => r(2))
      val amount      = sParse[InAmount](r => r(3))
      val subcategory = sParse[String](r => r(4))
      val memo        = sParse[String](r => r(5))
      number :: date :: account :: amount :: subcategory :: memo :: HNil
    }
  }

  def cleanInputTransaction(transactions: BarclaysTransaction): BarclaysTransaction = {
    val cleaned = Generic[BarclaysTransaction]
      .to(transactions)
      .map(defaultCleaner)
    Generic[BarclaysTransaction].from(cleaned)
  }

  def toTransaction(input: BarclaysTransaction): Transaction = {
    Transaction(
      date = input.date,
      amount = input.amount.value,
      description = input.memo,
      input = "Barclays",
      category = "",
      subcategory = "",
      notes = "",
    )
  }

}

/**
 * Barclays CSV data processor
 */
class BarclaysCsvInput extends BarclaysApiInput {
  override def filterInput: RawInputFilter = dropHeader andThen dropEmptyRows
}
