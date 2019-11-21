package com.colofabrix.scala.accounting.model

import com.colofabrix.scala.accounting.model.CsvDefinitions._
import monix.reactive.Observable

import scala.util._


/**
 * A CSV cleaner for a specific Bank
 */
trait CsvCleaner[A] {
  /**
   * File cleanups specific of the Bank
   */
  def cleanFile(row: Observable[List[String]]): Observable[List[String]]
}


object InputCleaning {

  /**
   * Processes the entire Csv file for cleanups
   */
  def fileCleaning[A](
      file: Observable[List[String]])(implicit bankCleaner: CsvCleaner[A]
  ): Observable[List[String]] = {
    for {
      row       <- bankCleaner.cleanFile(file)
      cleanCell <- rowCleaning(row)
    } yield {
      cleanCell
    }
  }

  /**
   * Processes an entire row for cleanups
   */
  def rowCleaning(row: List[String]): Observable[List[String]] = Observable {
    for {
      cell  <- row
      step1 <- basicCleaning(cell)
      step2 <- replaceMatches(step1)
    } yield {
      step2
    }
  }

  /**
   * Performs basic cell cleaning
   */
  def basicCleaning(cell: String): List[String] = List {
    cell
      .trim()
      .toLowerCase()
      .replaceAll("\\s{2,}", " ")
  }

  /**
   * Removes punctuation from the text
   */
  def removePunctuation(cell: String): List[String] = List {
    cell.replaceAll("""[\p{Punct}&&[^.]]""", "")
  }

  /**
   * Replaces matches in the cell for cleanups
   */
  def replaceMatches(cell: String): List[String] = List {
    cell
  }

}
