package com.colofabrix.scala.accounting.etl

import com.colofabrix.scala.accounting.etl.csv.AllInputs._
import com.colofabrix.scala.accounting.etl.csv.CsvProcessor
import com.colofabrix.scala.accounting.model._

class BarclaysInputConversionSpec extends InputConversionSpec[BarclaysTransaction] with BarclaysTestData {
  implicit val csvProcessor: CsvProcessor[BarclaysTransaction] = barclaysCsvProc

  s"The number of converted records for ${name}" should "the same as the number of input records" in {
    val result = read(this.sampleCorrectCsvData).through(csvProcessor.process)
    withValidatedIoStream(result) { computed =>
      // The correction value is based on the expected sample input data
      computed.length should equal (this.sampleCorrectCsvData.length - 3)
    }
  }
}

class HalifaxInputConversionSpec extends InputConversionSpec[HalifaxTransaction] with HalifaxTestData {
  implicit val csvProcessor: CsvProcessor[HalifaxTransaction] = halifaxCsvProc

  s"The number of converted records for ${name}" should "the same as the number of input records" in {
    val result = read(this.sampleCorrectCsvData).through(csvProcessor.process)
    withValidatedIoStream(result) { computed =>
      // The correction value is based on the expected sample input data
      computed.length should equal (this.sampleCorrectCsvData.length - 2)
    }
  }
}

class AmexInputConversionSpec extends InputConversionSpec[AmexTransaction] with AmexTestData {
  implicit val csvProcessor: CsvProcessor[AmexTransaction] = amexCsvProc

  s"The number of converted records for ${name}" should "the same as the number of input records" in {
    val result = read(this.sampleCorrectCsvData).through(csvProcessor.process)
    withValidatedIoStream(result) { computed =>
      // The correction value is based on the expected sample input data
      computed.length should equal (this.sampleCorrectCsvData.length - 1)
    }
  }
}

class StarlingInputConversionSpec extends InputConversionSpec[StarlingTransaction] with StarlingTestData {
  implicit val csvProcessor: CsvProcessor[StarlingTransaction] = starlingCsvProc

  s"The number of converted records for ${name}" should "the same as the number of input records" in {
    val result = read(this.sampleCorrectCsvData).through(csvProcessor.process)
    withValidatedIoStream(result) { computed =>
      // The correction value is based on the expected sample input data
      computed.length should equal (this.sampleCorrectCsvData.length - 3)
    }
  }
}
