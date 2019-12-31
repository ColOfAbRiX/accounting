package com.colofabrix.scala.accounting.etl.inputs

import java.time.LocalDate
import com.colofabrix.scala.accounting.etl.csv._
import com.colofabrix.scala.accounting.etl.FieldConverter._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.RecordConverter
import com.colofabrix.scala.accounting.etl.csv.CsvProcessorUtils._
import com.colofabrix.scala.accounting.model.BarclaysTransaction
import com.colofabrix.scala.accounting.utils.validation._
import shapeless._
import com.colofabrix.scala.accounting.model.Transaction

/**
 * Barclays CSV file processor
 */
class BarclaysCsvProcessor
    extends CsvProcessor[BarclaysTransaction]
    with RecordConverter[BarclaysTransaction]
    with Transformer[BarclaysTransaction] {

  protected def filter: RawInputFilter = dropHeader andThen dropEmptyRows

  protected def convert(record: RawRecord): AValidated[BarclaysTransaction] = {
    convertRecord(record) {
      val number      = parse[Option[Int]](r => r(0))
      val date        = parse[LocalDate](r => r(1))("dd/MM/yyyy")
      val account     = parse[String](r => r(2))
      val amount      = parse[BigDecimal](r => r(3))
      val subcategory = parse[String](r => r(4))
      val memo        = parse[String](r => r(5))
      number :: date :: account :: amount :: subcategory :: memo :: HNil
    }
  }

  def transform(input: BarclaysTransaction): Transaction = {
    Transaction(input.date, input.amount, input.memo, "Barclays", "", "", "")
  }

}