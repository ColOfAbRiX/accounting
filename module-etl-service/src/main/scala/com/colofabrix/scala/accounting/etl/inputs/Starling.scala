package com.colofabrix.scala.accounting.etl.inputs

import com.colofabrix.scala.accounting.etl.conversion._
import com.colofabrix.scala.accounting.etl.conversion.FieldConverter._
import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.etl.pipeline._
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
    val date           = parse[LocalDate](r => r(0))("dd/MM/yyyy")
    val counterParty   = parse[String](r => r(1))
    val reference      = parse[NonEmptyString](r => r(2))
    val `type`         = parse[String](r => r(3))
    val amount         = parse[BigDecimal](r => r(4))
    val balance        = parse[BigDecimal](r => r(5))
    val starlingParser = date :: counterParty :: reference :: `type` :: amount :: balance :: HNil

    val converter = new RecordConverter[StarlingTransaction] {}
    converter.convertRecord(record)(starlingParser)
  }

  def cleanInputTransaction(transaction: StarlingTransaction): AValidated[StarlingTransaction] = {
    genericCleaner(CleanerUtils.defaultCleaner)(transaction)
  }

  def toTransaction(input: StarlingTransaction): AValidated[SingleTransaction] =
    input
      .into[SingleTransaction]
      .withFieldConst(_.id, UUID.randomUUID)
      .withFieldConst(_.input, StarlingBank)
      .withFieldRenamed(_.reference, _.description)
      .withFieldConst(_.category, "")
      .withFieldConst(_.subcategory, "")
      .withFieldConst(_.notes, "")
      .transform
      .aValid

}

/**
 * Starling CSV data processor
 */
class StarlingCsvInput extends StarlingApiInput {
  override def filterInput: RawInputFilter = {
    dropHeader andThen dropEmptyRows andThen dropAnyMatch(_.toLowerCase.contains("opening balance"))
  }
}
