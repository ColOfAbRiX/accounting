package com.colofabrix.scala.accounting.banks

import java.time.LocalDate
import com.colofabrix.scala.accounting.csv.CsvConverter
import com.colofabrix.scala.accounting.csv.CsvDefinitions._
import com.colofabrix.scala.accounting.csv.CsvFieldParser._
import com.colofabrix.scala.accounting.model.BarclaysTransaction
import com.colofabrix.scala.accounting.utils.AValidation._
import shapeless._
import shapeless.syntax.std.tuple._

object Barclays {

  /**
    * Barclays Csv File Worker
    */
  object BarclaysCsvFile extends CsvConverter[BarclaysTransaction] {
    /** Converts a Csv row into a BankTransaction */
    protected def filterFile(file: CsvFile): AValidated[CsvFile] = {
      file
        .drop(1)
        .filter(row => row.nonEmpty)
        .aValid
    }

    /** Converts a Csv row into a BankTransaction */
    protected def convertRow(row: CsvRow): AValidated[BarclaysTransaction] = {
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

  implicit val barclaysCsvConverter: CsvConverter[BarclaysTransaction] = BarclaysCsvFile

}
