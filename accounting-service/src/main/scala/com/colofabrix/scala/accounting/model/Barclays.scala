package com.colofabrix.scala.accounting.model

import java.time.LocalDate
import cats.data._
import cats.data.Validated
import cats.implicits._
import cats.syntax.apply._
import com.colofabrix.scala.accounting.csv._
import com.colofabrix.scala.accounting.csv.CsvDefinitions._
import monix.reactive.Observable
import shapeless._
import shapeless.ops._
import shapeless.ops.hlist._
import shapeless.syntax.typeable._
import shapeless.syntax.std.tuple._
import shapeless.syntax.std.traversable._

object Barclays {

  /**
   * Transaction on a Barclays CSV file
   */
  final case class BarclaysTransaction(
    number: Int,
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

    def cleanFile(file: CsvStream): Observable[List[String]] = {
      for {
        row <- file.drop(1)
      } yield {
        row
      }
    }

    val fieldsDefinition = HList(
      CsvFieldDef(0, "number",      parse[Int]),
      CsvFieldDef(1, "date",        parse[LocalDate](dateFormat)),
      CsvFieldDef(2, "account",     parse[String]),
      CsvFieldDef(3, "amount",      parse[BigDecimal]),
      CsvFieldDef(4, "subcategory", parse[String]),
      CsvFieldDef(5, "memo",        parse[String])
    )

    type RowType =
      String ::
      String ::
      String ::
      String ::
      String ::
      String ::
      HNil

    def convertRow(row: List[String]): CsvValidationResult[BarclaysTransaction] = {
      object convert extends Poly1 {
        implicit def converter[A] = at[CsvFieldDef[A]](_.convert)
      }

      val hFieldConverters = fieldsDefinition.map(convert)
      val hFieldValues = row.toHList[RowType].get
      val convertedFields = hFieldConverters.zipApply(hFieldValues).tupled

      val validatedTransaction = convertedFields.mapN(BarclaysTransaction)
      validatedTransaction
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
  }

}
