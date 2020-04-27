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
import com.colofabrix.scala.accounting.model.BankType.AmexBank
import com.colofabrix.scala.accounting.utils.validation._
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString
import io.scalaland.chimney.dsl._
import java.util.UUID
import java.time.LocalDate
import shapeless._

/**
 * Amex API data processor
 */
class AmexApiInput
    extends InputProcessor[AmexTransaction]
    with Cleaner[AmexTransaction]
    with Normalizer[AmexTransaction] {

  def filterInput: RawInputFilter = identity

  def convertRaw(record: RawRecord): AValidated[AmexTransaction] = {
    val date        = parse[LocalDate](r => r(0))("dd/MM/yyyy")
    val reference   = parse[NonEmptyString](r => r(1))
    val amount      = parse[BigDecimal](r => r(2)).map(x => -1.0 * x)
    val description = parse[String](r => r(3))
    val extra       = parse[String](r => r(4))

    val amexParsers = date :: reference :: amount :: description :: extra :: HNil
    val converter   = new RecordConverter[AmexTransaction] {}

    converter.convertRecord(record)(amexParsers)
  }

  case class AmexTransaction2(
      date: LocalDate,
      reference: String,
      amount: BigDecimal,
      description: NonEmptyString,
      extra: String,
  )

  def cleanInputTransaction(transactions: AmexTransaction): AmexTransaction = {
    val gen     = Generic[AmexTransaction]
    val to      = gen.to(transactions)
    val cleaned = to.map(defaultCleaner)
    val from    = gen.from(cleaned)
    from
  }

  def toTransaction(input: AmexTransaction): SingleTransaction =
    input
      .into[SingleTransaction]
      .withFieldConst(_.id, UUID.randomUUID)
      .withFieldConst(_.input, AmexBank)
      .withFieldConst(_.category, "")
      .withFieldConst(_.subcategory, "")
      .withFieldConst(_.notes, "")
      .transform

}

/**
 * Amex CSV data processor
 */
class AmexCsvInput extends AmexApiInput {
  override def filterInput: RawInputFilter = dropEmptyRows
}
