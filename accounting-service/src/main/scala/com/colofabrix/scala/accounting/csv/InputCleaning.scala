package com.colofabrix.scala.accounting.csv

import com.colofabrix.scala.accounting.csv.CsvDefinitions._
import monix.reactive.Observable


object InputCleaning {

  /** Processes the entire Csv file for cleanups */
  def cleanFile(file: CsvStream): CsvStream = {
    for {
      row  <- file
      cell <- Observable(cleanRow(row))
    } yield {
      cell
    }
  }

  /** Cleans a single row of a Csv */
  def cleanRow(row: CsvRow): CsvRow = {
    for {
      cell <- row
    } yield {
      cell.trim().toLowerCase().replaceAll("\\s{2,}", " ")
    }
  }

}
