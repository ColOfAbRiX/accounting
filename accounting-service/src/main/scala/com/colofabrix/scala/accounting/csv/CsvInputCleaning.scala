package com.colofabrix.scala.accounting.csv

import com.colofabrix.scala.accounting.csv.CsvDefinitions.CsvRow


/**
  * Basic cleanings for inputs
  */
object CsvInputCleaning {

  def cleanRow(row: CsvRow): CsvRow = {
    row.map { cell =>
      cell.trim.toLowerCase
    }
  }

}
