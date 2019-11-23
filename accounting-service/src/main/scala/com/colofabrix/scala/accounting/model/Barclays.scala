package com.colofabrix.scala.accounting.model

import java.time.LocalDate
import cats.data.Validated
import cats.implicits._
import com.colofabrix.scala.accounting.csv._
import com.colofabrix.scala.accounting.csv.CsvDefinitions._
import monix.reactive.Observable
import shapeless._
import shapeless.ops.hlist._
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
  object BarclaysCsvFile extends CsvCleaner[BarclaysTransaction] with CsvConverter[BarclaysTransaction] {
    import CsvRawTypeParser._

    val dateFormat = "dd/MM/yyyy"

    // Cleaner

    def cleanFile(file: CsvStream): Observable[List[String]] = {
      for {
        row <- file.drop(1)
      } yield {
        row
      }
    }

    val fieldsDefinition = HList(
      CsvFieldDef(0, "number",      parse[Option[Int]]),
      CsvFieldDef(1, "date",        parse[LocalDate](dateFormat)),
      CsvFieldDef(2, "account",     parse[String]),
      CsvFieldDef(3, "amount",      parse[BigDecimal]),
      CsvFieldDef(4, "subcategory", parse[String]),
      CsvFieldDef(5, "memo",        parse[String])
    )

    type Converters =
      (String => CsvValidated[Option[Int]]) ::
      (String => CsvValidated[LocalDate]) ::
      (String => CsvValidated[String]) ::
      (String => CsvValidated[scala.BigDecimal]) ::
      (String => CsvValidated[String]) ::
      (String => CsvValidated[String]) ::
      HNil

    type RowType =
      String ::
      String ::
      String ::
      String ::
      String ::
      String ::
      HNil

    //val fieldsDefinition = CsvField(2, "account", cell => stringParser.parse(cell)) :: HNil
    //type Converters = (String => CsvValidated[String]) :: HNil
    //type RowType = String :: HNil

    // Converter

    def convertRow(row: List[String]): CsvValidated[BarclaysTransaction] = {

      object extractConverters extends Poly1 {
        implicit def converter[A] = at[CsvFieldDef[A]](_.convert)
      }

      val converters = fieldsDefinition.map(extractConverters)
      val values = row.toHList[RowType].get

      val zipApply = ZipApply[Converters, RowType]
      val result = zipApply(converters, values).tupled

      ???
    }

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

  //implicit val barclaysConverter: CsvConverter[BarclaysTransaction] = new BarclaysCsvFile()
  //
  //implicit val barclaysCsvCleaner: CsvCleaner[BarclaysTransaction] = new BarclaysCsvFile()

}
