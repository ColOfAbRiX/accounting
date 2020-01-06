package com.colofabrix.scala.accounting.etl.inputs

import cats.data.Kleisli
import com.colofabrix.scala.accounting.etl.csv._
import com.colofabrix.scala.accounting.etl.csv.CsvProcessorUtils._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.FieldConverter._
import com.colofabrix.scala.accounting.etl.pipeline.CleanerUtils._
import com.colofabrix.scala.accounting.etl.GenericConverter
import com.colofabrix.scala.accounting.etl.pipeline._
import com.colofabrix.scala.accounting.etl.RecordConverter
import com.colofabrix.scala.accounting.model.BarclaysTransaction
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._
import java.time.LocalDate
import shapeless._

/**
 * Barclays CSV file processor
 */
class BarclaysInputProcessor
    extends CsvProcessor[BarclaysTransaction]
    with RecordConverter[BarclaysTransaction]
    with Cleaner[BarclaysTransaction]
    with Normalizer[BarclaysTransaction] {

  protected def filter: RawInputFilter = dropHeader andThen dropEmptyRows

  protected def convert(record: RawRecord): AValidated[BarclaysTransaction] = {
    convertRecord(record) {
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
