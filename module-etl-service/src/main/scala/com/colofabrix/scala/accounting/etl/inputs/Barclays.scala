package com.colofabrix.scala.accounting.etl.inputs

import cats.implicits._
import com.colofabrix.scala.accounting.etl._
import com.colofabrix.scala.accounting.etl.conversion.FieldConverter._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.etl.pipeline._
import com.colofabrix.scala.accounting.etl.pipeline.CleanerUtils._
import com.colofabrix.scala.accounting.etl.pipeline.InputProcessorUtils._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.model.BankType.BarclaysBank
import com.colofabrix.scala.accounting.utils.validation._
import io.scalaland.chimney.dsl._
import java.{ util => jutils }
import java.time.LocalDate
import shapeless._

// package object chimney {
//   import eu.timepit.refined.api.{RefType, Validate}
//   import scala.reflect.runtime.universe.WeakTypeTag
//   import io.scalaland.chimney.Transformer
//   implicit def refTypeTransformer[F[_, _], T, P](
//       implicit
//       transformer: Transformer[T, P],
//       refType: RefType[F],
//       validate: Validate[T, P],
//       typeTag: WeakTypeTag[F[T, P]]
//   ): Transformer[T, F[P]] = ???
// }

import eu.timepit.refined._
import eu.timepit.refined.auto._
import eu.timepit.refined.collection._
import eu.timepit.refined.shapeless.typeable._
import eu.timepit.refined.cats._

/**
 * Barclays API data processor
 */
class BarclaysApiInput
    extends InputProcessor[BarclaysTransaction]
    with Cleaner[BarclaysTransaction]
    with Normalizer[BarclaysTransaction] {

  def filterInput: RawInputFilter = identity

  def convertRaw(record: RawRecord): AValidated[BarclaysTransaction] = {
    val converter = new RecordConverter[BarclaysTransaction] {}
    converter.convertRecord(record) {
      val number      = sParse[Option[Int]](r => r(0))
      val date        = sParse[LocalDate](r => r(1))("dd/MM/yyyy")
      val account     = sParse[String](r => r(2))
      val amount      = sParse[BigDecimal](r => r(3))
      val subcategory = sParse[String](r => r(4))
      val memo        = sParse[String](r => r(5)).map(refineV[NonEmpty](_))
      number :: date :: account :: amount :: subcategory :: memo :: HNil
    }
  }

  def cleanInputTransaction(transactions: BarclaysTransaction): BarclaysTransaction =
    Generic[BarclaysTransaction].from {
      Generic[BarclaysTransaction]
        .to(transactions)
        .map(defaultCleaner)
    }

  def toTransaction(input: BarclaysTransaction): SingleTransaction =
    input
      .into[SingleTransaction]
      .withFieldConst(_.id, jutils.UUID.randomUUID)
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
