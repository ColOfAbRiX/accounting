package com.colofabrix.scala.accounting.csv

import cats.implicits._
import com.colofabrix.scala.accounting.banks.Halifax._
import com.colofabrix.scala.accounting.banks.Barclays._
import com.colofabrix.scala.accounting.banks.Amex._
import com.colofabrix.scala.accounting.banks.Starling._
import com.colofabrix.scala.accounting.utils.AValidation._
import org.scalatest.{FlatSpec, Matchers}
import com.colofabrix.scala.accounting.model._
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.scalatest._

class TransactionConversionSpec extends FlatSpec with Matchers with ValidatedMatchers {

  "The input CSV for Barclays" should "be converted into a sequence of BarclaysTransaction" in {
    val computed = BarclaysCsvFile.convertFile(CsvData.barclaysCsv)
    val expected = CsvData.barclaysTransactions
    computed shouldBe valid
    computed.map { transactions =>
      transactions should contain theSameElementsInOrderAs (expected)
    }
  }

  "The input CSV for Halifax" should "be converted into a sequence of HalifaxTransaction" in {
    val computed = HalifaxCsvFile.convertFile(CsvData.halifaxCsv)
    val expected = CsvData.halifaxTransactions
    computed shouldBe valid
    computed.map { transactions =>
      transactions should contain theSameElementsInOrderAs (expected)
    }
  }

  "The input CSV for American Express" should "be converted into a sequence of AmexTransaction" in {
    val computed = AmexCsvFile.convertFile(CsvData.amexCsv)
    val expected = CsvData.amexTransactions
    computed shouldBe valid
    computed.map { transactions =>
      transactions should contain theSameElementsInOrderAs (expected)
    }
  }

  "The input CSV for Starling" should "be converted into a sequence of StarlingTransaction" in {
    val computed = StarlingCsvFile.convertFile(CsvData.starlingCsv)
    val expected = CsvData.starlingTransactions
    computed shouldBe valid
    computed.map { transactions =>
      transactions should contain theSameElementsInOrderAs (expected)
    }
  }

}
