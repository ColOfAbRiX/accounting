package com.colofabrix.scala.accounting.etl.csv

import inputs._
import com.colofabrix.scala.accounting.model._

/**
 * Imports all defined inputs into one scope
 */
object AllInputs {

  import java.time.LocalDate
  import com.colofabrix.scala.accounting.etl.csv._
  import com.colofabrix.scala.accounting.etl.FieldConverter._
  import com.colofabrix.scala.accounting.etl.definitions._
  import com.colofabrix.scala.accounting.model.BarclaysTransaction
  import com.colofabrix.scala.accounting.utils.validation._
  import shapeless._
  import com.colofabrix.scala.accounting.etl.RecordConverter

  implicit val barclaysCsvProc: CsvProcessor[BarclaysTransaction] = new BarclaysCsvProcessor()
  implicit val halifaxCsvProc: CsvProcessor[HalifaxTransaction]   = new HalifaxCsvProcessor()
  implicit val starlingCsvProc: CsvProcessor[StarlingTransaction] = new StarlingCsvProcessor()
  implicit val amexCsvProc: CsvProcessor[AmexTransaction]         = new AmexCsvProcessor()

}
