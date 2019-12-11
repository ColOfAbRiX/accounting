package com.colofabrix.scala.accounting.csv

import monix.reactive.Observable


object CsvDefinitions {

  /** A line of the Csv file, simply a List[String */
  type CsvRow = List[String]

  /** A CsvStream is a steam of CsvRows */
  type CsvStream = Observable[CsvRow]

}
