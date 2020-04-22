package com.colofabrix.scala.accounting.etl.inputs

import cats.implicits._
import com.colofabrix.scala.accounting.etl._
import com.colofabrix.scala.accounting.etl.conversion.FieldConverter._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.etl.pipeline._
import com.colofabrix.scala.accounting.etl.pipeline.CleanerUtils._
import com.colofabrix.scala.accounting.etl.pipeline.InputProcessorUtils._
import com.colofabrix.scala.accounting.etl.refined.conversion._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.model.BankType.StarlingBank
import com.colofabrix.scala.accounting.utils.validation._
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString
import io.scalaland.chimney.dsl._
import java.util.UUID
import java.time.LocalDate
import shapeless._

/**
 * Starling API data processor
 */
class StarlingApiInput
    extends InputProcessor[StarlingTransaction]
    with Cleaner[StarlingTransaction]
    with Normalizer[StarlingTransaction] {

  def filterInput: RawInputFilter = identity

  def convertRaw(record: RawRecord): AValidated[StarlingTransaction] = {
    val converter = new RecordConverter[StarlingTransaction] {}
    converter.convertRecord(record) {
      val date         = sParse[LocalDate](r => r(0))("dd/MM/yyyy")
      val counterParty = sParse[String](r => r(1))
      val reference    = sParse[NonEmptyString](r => r(2))
      val `type`       = sParse[String](r => r(3))
      val amount       = sParse[BigDecimal](r => r(4))
      val balance      = sParse[BigDecimal](r => r(5))
      date :: counterParty :: reference :: `type` :: amount :: balance :: HNil
    }
  }

  def cleanInputTransaction(transactions: StarlingTransaction): StarlingTransaction = {
    val gen     = Generic[StarlingTransaction]
    val to      = gen.to(transactions)
    val cleaned = to.map(defaultCleaner)
    val from    = gen.from(cleaned)
    from
  }

  def toTransaction(input: StarlingTransaction): SingleTransaction =
    input
      .into[SingleTransaction]
      .withFieldConst(_.id, UUID.randomUUID)
      .withFieldConst(_.input, StarlingBank)
      .withFieldRenamed(_.reference, _.description)
      .withFieldConst(_.category, "")
      .withFieldConst(_.subcategory, "")
      .withFieldConst(_.notes, "")
      .transform

}

/**
 * Starling CSV data processor
 */
class StarlingCsvInput extends StarlingApiInput {
  override def filterInput: RawInputFilter = {
    dropHeader andThen dropEmptyRows andThen dropAnyMatch(_.toLowerCase.contains("opening balance"))
  }
}
