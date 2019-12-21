package com.colofabrix.scala.accounting.etl

import cats.implicits._
import cats.scalatest._
import com.colofabrix.scala.accounting.utils.validation._
import org.scalatest.{ FlatSpec, Matchers }
import com.colofabrix.scala.accounting.model._
import csv._
import csv.inputs._
import AllInputs._


trait InputConversion[T <: InputTransaction]
    extends FlatSpec
    with Matchers
    with ValidatedMatchers { this: InputTestData[T] =>

  def name: String
  def csvProcessor: CsvProcessor[T]

  s"The input data for ${name}" should s"be converted into a sequence of ${name}Transaction" in {
    val reader = new DummyCsvReader(this.sampleCorrectCsvData)
    val converter = new CsvInputConverter[T](reader)(csvProcessor)

    val computedV = converter.ingestInput
    val expectedV = this.convertedCorrectData.aValid

    computedV shouldBe valid
    (computedV, expectedV).mapN { (computed, expected) =>
      computed should contain theSameElementsInOrderAs (expected)
    }
  }

}

class BarclaysInputConversion extends InputConversion[BarclaysTransaction] with BarclaysTestData {
  def name = "Barclays"
  def csvProcessor = barclaysCsvProc
}

class HalifaxInputConversion extends InputConversion[HalifaxTransaction] with HalifaxTestData {
  def name = "Halifax"
  def csvProcessor = halifaxCsvProc
}

class StarlingInputConversion extends InputConversion[StarlingTransaction] with StarlingTestData {
  def name = "Starling"
  def csvProcessor = starlingCsvProc
}

class AmexInputConversion extends InputConversion[AmexTransaction] with AmexTestData {
  def name = "Amex"
  def csvProcessor = amexCsvProc
}

// class TransactionConversionSpec extends FlatSpec with Matchers with ValidatedMatchers {

//   "The input CSV for Barclays" should "be converted into a sequence of BarclaysTransaction" in {
//     val computed = BarclaysCsvFile.convertFile(CsvData.barclaysCsv)
//     val expected = CsvData.barclaysTransactions
//     computed shouldBe valid
//     computed.map { transactions =>
//       transactions should contain theSameElementsInOrderAs (expected)
//     }
//   }

//   "The input CSV for Halifax" should "be converted into a sequence of HalifaxTransaction" in {
//     val computed = HalifaxCsvFile.convertFile(CsvData.halifaxCsv)
//     val expected = CsvData.halifaxTransactions
//     computed shouldBe valid
//     computed.map { transactions =>
//       transactions should contain theSameElementsInOrderAs (expected)
//     }
//   }

//   "The input CSV for American Express" should "be converted into a sequence of AmexTransaction" in {
//     val computed = AmexCsvFile.convertFile(CsvData.amexCsv)
//     val expected = CsvData.amexTransactions
//     computed shouldBe valid
//     computed.map { transactions =>
//       transactions should contain theSameElementsInOrderAs (expected)
//     }
//   }

//   "The input CSV for Starling" should "be converted into a sequence of StarlingTransaction" in {
//     val computed = StarlingCsvFile.convertFile(CsvData.starlingCsv)
//     val expected = CsvData.starlingTransactions
//     computed shouldBe valid
//     computed.map { transactions =>
//       transactions should contain theSameElementsInOrderAs (expected)
//     }
//   }

// }
