package com.colofabrix.scala.accounting.etl

import com.colofabrix.scala.accounting.etl.csv.CsvProcessor
import com.colofabrix.scala.accounting.etl.inputs._
import com.colofabrix.scala.accounting.model._

class BarclaysInputConversionSpec extends InputConversionSpec[BarclaysTransaction] with BarclaysTestData {
  implicit val csvProcessor: CsvProcessor[BarclaysTransaction] = new BarclaysCsvProcessor()
}

class HalifaxInputConversionSpec extends InputConversionSpec[HalifaxTransaction] with HalifaxTestData {
  implicit val csvProcessor: CsvProcessor[HalifaxTransaction] = new HalifaxCsvProcessor()
}

class AmexInputConversionSpec extends InputConversionSpec[AmexTransaction] with AmexTestData {
  implicit val csvProcessor: CsvProcessor[AmexTransaction] = new AmexCsvProcessor()
}

class StarlingInputConversionSpec extends InputConversionSpec[StarlingTransaction] with StarlingTestData {
  implicit val csvProcessor: CsvProcessor[StarlingTransaction] = new StarlingCsvProcessor()
}
