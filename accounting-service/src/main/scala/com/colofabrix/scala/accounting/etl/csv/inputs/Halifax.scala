package com.colofabrix.scala.accounting.etl.csv.inputs

import java.time.LocalDate
import com.colofabrix.scala.accounting.etl.csv._
import com.colofabrix.scala.accounting.etl.FieldConverter._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.RecordConverter
import com.colofabrix.scala.accounting.model.HalifaxTransaction
import com.colofabrix.scala.accounting.utils.validation._
import CsvProcessorUtils._
import shapeless._

/**
 * Halifax CSV file processor
 */
class HalifaxCsvProcessor extends CsvProcessor[HalifaxTransaction] with RecordConverter[HalifaxTransaction] {

  protected def filter: RawInputFilter = dropHeader andThen dropEmptyRows

  protected def convert(record: RawRecord): AValidated[HalifaxTransaction] = {
    convertRecord(record) {
      val date        = parse[LocalDate](r => r(0))("dd/MM/yyyy")
      val dateEntered = parse[LocalDate](r => r(1))("dd/MM/yyyy")
      val reference   = parse[String](r => r(2))
      val description = parse[String](r => r(3))
      val amount      = parse[BigDecimal](r => r(4)).map(amount => -1.0 * amount)
      date :: dateEntered :: reference :: description :: amount :: HNil
    }
  }

}
