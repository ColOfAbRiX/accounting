package com.colofabrix.scala.accounting.etl.csv.inputs

import java.time.LocalDate
import com.colofabrix.scala.accounting.etl.csv._
import com.colofabrix.scala.accounting.etl.FieldConverter._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.RecordConverter
import com.colofabrix.scala.accounting.model.BarclaysTransaction
import com.colofabrix.scala.accounting.utils.validation._
import shapeless._

/**
 * Barclays CSV file processor
 */
class BarclaysCsvProcessor extends CsvProcessor[BarclaysTransaction] with RecordConverter[BarclaysTransaction] {

  protected def filter(input: VRawInput[fs2.Pure]): VRawInput[fs2.Pure] = {
    dropEmptyRows(dropHeader(input))
  }

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

}
