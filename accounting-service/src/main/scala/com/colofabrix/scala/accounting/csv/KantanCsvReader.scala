package com.colofabrix.scala.accounting.csv

import java.io.File
import scala.util._
import com.colofabrix.scala.accounting.csv.CsvDefinitions._
import com.colofabrix.scala.accounting.utils.AValidation._
import monix.reactive.Observable


/**
 * Kantan CSV Reader
 */
class KantanCsvReader extends CsvReader {
  import kantan.csv._
  import kantan.csv.ops._

  def readFile(file: File): AValidated[CsvStream] = {
    Try {
      val csvReader = file.asUnsafeCsvReader[List[String]](rfc)
      Observable.fromIterable(csvReader.toIterable)
    }.toAValidated
  }

}
