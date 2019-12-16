package com.colofabrix.scala.accounting.csv

object CsvDefinitions {

  /** A line of the Csv file, simply a List[String */
  type CsvRow = List[String]

  /** A CsvStream is a steam of CsvRows */
  type CsvFile = List[CsvRow]

}
