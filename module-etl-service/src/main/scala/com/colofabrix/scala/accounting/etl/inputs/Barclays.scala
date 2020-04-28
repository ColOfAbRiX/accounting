package com.colofabrix.scala.accounting.etl.inputs

import cats.implicits._
import com.colofabrix.scala.accounting.etl.conversion._
import com.colofabrix.scala.accounting.etl.conversion.FieldConverter._
import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.etl.pipeline._
import com.colofabrix.scala.accounting.etl.pipeline.CleanerUtils._
import com.colofabrix.scala.accounting.etl.pipeline.InputProcessorUtils._
import com.colofabrix.scala.accounting.etl.refined.conversion._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.model.BankType.BarclaysBank
import com.colofabrix.scala.accounting.utils.validation._
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString
import io.scalaland.chimney.dsl._
import java.util.UUID
import java.time.LocalDate
import shapeless._

/**
 * Barclays API data processor
 */
class BarclaysApiInput
    extends InputProcessor[BarclaysTransaction]
    with Cleaner[BarclaysTransaction]
    with Normalizer[BarclaysTransaction] {

  def filterInput: RawInputFilter = identity

  def convertRaw(record: RawRecord): AValidated[BarclaysTransaction] = {
    val number         = parse[Option[Int]](r => r(0))
    val date           = parse[LocalDate](r => r(1))("dd/MM/yyyy")
    val account        = parse[String](r => r(2))
    val amount         = parse[BigDecimal](r => r(3))
    val subcategory    = parse[String](r => r(4))
    val memo           = parse[NonEmptyString](r => r(5))
    val barclaysParser = number :: date :: account :: amount :: subcategory :: memo :: HNil

    val converter = new RecordConverter[BarclaysTransaction] {}
    converter.convertRecord(record)(barclaysParser)
  }

  def cleanInputTransaction(transactions: BarclaysTransaction): BarclaysTransaction = {
    val gen     = Generic[BarclaysTransaction]
    val to      = gen.to(transactions)
    val cleaned = to.map(defaultCleaner)
    val from    = gen.from(cleaned)
    from
  }

  def toTransaction(input: BarclaysTransaction): SingleTransaction =
    input
      .into[SingleTransaction]
      .withFieldConst(_.id, UUID.randomUUID)
      .withFieldConst(_.input, BarclaysBank)
      .withFieldRenamed(_.memo, _.description)
      .withFieldConst(_.category, "")
      .withFieldConst(_.subcategory, "")
      .withFieldConst(_.notes, "")
      .transform

}

/**
 * Barclays CSV data processor
 */
class BarclaysCsvInput extends BarclaysApiInput {
  override def filterInput: RawInputFilter = dropHeader andThen dropEmptyRows
}
