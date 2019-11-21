package com.colofabrix.scala.accounting.model

import java.time.LocalDate
import cats.implicits._
import com.colofabrix.scala.accounting.model.CsvDefinitions._
import monix.reactive.Observable
import shapeless._
import shapeless.ops.tuple.ZipApply
import shapeless.syntax.std.tuple._
import shapeless.syntax.std.traversable._


object Barclays {

  /**
   * Transaction on a Barclays CSV file
   */
  final case class BarclaysTransaction(
    number: Option[Int],
    date: LocalDate,
    account: String,
    amount: BigDecimal,
    subcategory: String,
    memo: String
  ) extends BankTransaction

  /**
   * Barclays Csv File Worker
   */
  final class BarclaysCsvFile extends CsvCleaner[BarclaysTransaction] with CsvConverter[BarclaysTransaction] {

    // Cleaner

    def cleanFile(file: Observable[List[String]]): Observable[List[String]] = {
      for {
        row <- file.drop(1)
      } yield {
        row
      }
  }

  val fieldsDefinition = HList(
    CsvField("number",      0, _ => 0.valid),
    CsvField("date",        1, parseLocalDate),
    CsvField("account",     2, parseString),
    CsvField("amount",      3, parseBigDecimal),
    CsvField("subcategory", 4, parseString),
    CsvField("memo",        5, parseString)
  )

  type ConstructorType = Int :: LocalDate :: String :: BigDecimal :: String :: String :: HNil

  // Converter

    def convertRowTemp(row: List[String]): List[BarclaysTransaction] = {
      object extractConverters extends Poly1 {
        implicit def converter[A] = at[CsvField[A]](_.convert)
      }

      val converters = fieldsDefinition.map(extractConverters)
      val values = row.toHList[ConstructorType].get

      val zipApply = ZipApply[converters.type, values.type]

      zipApply(converters, values)

      ???
    }

    val dateFormat = "dd/MM/yyyy"
    def convertRow(row: List[String]): CsvValidated[BarclaysTransaction] = ???
    //def convertRow(row: List[String]): Try[BarclaysTransaction] = ???
    //  implicit val implRow = row
    //
    //  for {
    //    number      <- Success(parseInt(0).toOption)
    //    date        <- parseLocalDate(1)
    //    account     <- parseString(2)
    //    amount      <- parseBigDecimal(3)
    //    subcategory <- parseString(4)
    //    memo        <- parseString(5)
    //  } yield {
    //    BarclaysTransaction(number, date, account, amount, subcategory, memo)
    //  }
    //}
    /**
      * Converts a Csv row into a BankTransaction
      */
  }

  implicit val barclaysConverter: CsvConverter[BarclaysTransaction] = new BarclaysCsvFile()

  implicit val barclaysCsvCleaner: CsvCleaner[BarclaysTransaction] = new BarclaysCsvFile()

}
