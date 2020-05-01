package com.colofabrix.scala.accounting.etl.inputs

import com.colofabrix.scala.accounting.etl.conversion._
import com.colofabrix.scala.accounting.etl.conversion.FieldConverter._
import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.etl.pipeline._
import com.colofabrix.scala.accounting.etl.pipeline.InputProcessorUtils._
import com.colofabrix.scala.accounting.etl.refined.conversion._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.model.BankType.HalifaxBank
import com.colofabrix.scala.accounting.utils.validation._
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString
import io.scalaland.chimney.dsl._
import java.util.UUID
import java.time.LocalDate
import shapeless._

/**
 * Halifax API data processor
 */
class HalifaxApiInput
    extends InputProcessor[HalifaxTransaction]
    with Cleaner[HalifaxTransaction]
    with Normalizer[HalifaxTransaction] {

  def filterInput: RawInputFilter = identity

  def convertRaw(record: RawRecord): AValidated[HalifaxTransaction] = {
    val date          = parse[LocalDate](r => r(0))("dd/MM/yyyy")
    val dateEntered   = parse[LocalDate](r => r(1))("dd/MM/yyyy")
    val reference     = parse[String](r => r(2))
    val description   = parse[NonEmptyString](r => r(3))
    val amount        = parse[BigDecimal](r => r(4)).map(x => -1.0 * x)
    val halifaxParser = date :: dateEntered :: reference :: description :: amount :: HNil

    val converter = new RecordConverter[HalifaxTransaction] {}
    converter.convertRecord(record)(halifaxParser)
  }

  def cleanInputTransaction(transaction: HalifaxTransaction): AValidated[HalifaxTransaction] = {
    genericCleaner(CleanerUtils.defaultCleaner)(transaction)
  }

  def toTransaction(input: HalifaxTransaction): AValidated[SingleTransaction] =
    input
      .into[SingleTransaction]
      .withFieldConst(_.id, UUID.randomUUID)
      .withFieldConst(_.input, HalifaxBank)
      .withFieldConst(_.category, "")
      .withFieldConst(_.subcategory, "")
      .withFieldConst(_.notes, "")
      .transform
      .aValid

}

/**
 * Halifax CSV data processor
 */
class HalifaxCsvInput extends HalifaxApiInput {
  override def filterInput: RawInputFilter = dropHeader andThen dropEmptyRows
}
