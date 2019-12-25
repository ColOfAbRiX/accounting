package com.colofabrix.scala.accounting.etl

object definitions {

  /** A raw record that comes from the input */
  type RawRecord = List[String]

  /** An input as collection of RawRecords */
  type RawInput = List[RawRecord]

}
