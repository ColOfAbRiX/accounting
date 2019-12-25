package com.colofabrix.scala.accounting.etl.csv

import com.colofabrix.scala.accounting.etl.csv.inputs._
import com.colofabrix.scala.accounting.model._

/**
 * Imports all defined inputs into one scope
 */
object AllInputs {

  implicit val barclaysCsvProc: CsvProcessor[BarclaysTransaction] = new BarclaysCsvProcessor()
  implicit val halifaxCsvProc: CsvProcessor[HalifaxTransaction]   = new HalifaxCsvProcessor()
  implicit val starlingCsvProc: CsvProcessor[StarlingTransaction] = new StarlingCsvProcessor()
  implicit val amexCsvProc: CsvProcessor[AmexTransaction]         = new AmexCsvProcessor()

}
