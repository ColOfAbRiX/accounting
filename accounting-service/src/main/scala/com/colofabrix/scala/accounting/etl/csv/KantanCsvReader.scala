package com.colofabrix.scala.accounting.etl.csv

import java.io.File
import scala.util._
import com.colofabrix.scala.accounting.etl.InputDefinitions._
import com.colofabrix.scala.accounting.utils.AValidation._

/**
  * Kantan CSV Reader
  */
class KantanCsvReader extends CsvReader {
  import kantan.csv._
  import kantan.csv.ops._

  def read(file: File): AValidated[RawInput] =
    Try {
      file.asUnsafeCsvReader[List[String]](rfc).toList
    }.toAValidated
}
