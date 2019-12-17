package com.colofabrix.scala.accounting.csv

import java.io.File
import scala.util._
import com.colofabrix.scala.accounting.csv.CsvDefinitions._
import com.colofabrix.scala.accounting.utils.AValidation._

/**
  * Kantan CSV Reader
  */
class KantanCsvReader extends CsvReader {
  import kantan.csv._
  import kantan.csv.ops._

  def readFile(file: File): AValidated[CsvFile] = {
    Try {
      val csvReader = file.asUnsafeCsvReader[List[String]](rfc)
      csvReader.toList
    }.toAValidated
  }

}
