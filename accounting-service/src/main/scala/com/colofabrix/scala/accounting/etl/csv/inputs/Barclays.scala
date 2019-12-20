package com.colofabrix.scala.accounting.etl.csv.inputs

import java.time.LocalDate
import com.colofabrix.scala.accounting.etl.csv._
import com.colofabrix.scala.accounting.etl.FieldConverter._
import com.colofabrix.scala.accounting.etl.InputDefinitions._
import com.colofabrix.scala.accounting.model.BarclaysTransaction
import com.colofabrix.scala.accounting.utils.AValidation._
import shapeless._
import com.colofabrix.scala.accounting.etl.RecordConverter

/**
 * Barclays CSV file processor
 */
class BarclaysCsvProcessor extends CsvProcessor[BarclaysTransaction] with RecordConverter[BarclaysTransaction] {

  /** Converts a Csv row into a BankTransaction */
  def filterFile(file: RawInput): RawInput = {
    file
      .drop(1)
      .filter(
        _.filter(_.nonEmpty).nonEmpty
      )
  }

  /** Converts a Csv row into a BankTransaction */
  def convertRow(row: RawRecord): AValidated[BarclaysTransaction] = {
    convert(row) {
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