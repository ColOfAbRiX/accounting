package com.colofabrix.scala.accounting.etl.pipeline

import cats._
import cats.data.Validated._
import cats.data._
import cats.effect._
import cats.implicits._
import cats.scalatest._
import com.colofabrix.scala.accounting.etl.inputs._
import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.etl.readers._
import com.colofabrix.scala.accounting.model.BankType._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.StreamHelpers
import com.colofabrix.scala.accounting.utils.validation._
import com.colofabrix.scala.accounting.utils.validation.streams._
import eu.timepit.refined.auto._
import fs2.{ Pure, Stream }
import java.time.LocalDate
import org.scalactic.Prettifier
import org.scalatest._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

//format: off

trait PPrintPrettifier {
  implicit val prettifier: Prettifier = new Prettifier {
    override def apply(o: Any): String = pprint.apply(o).toString() + "\n"
  }
}

trait PipelineTestingDefinition[T <: InputTransaction] {
  trait InputProcessorTestData {
    def correctInputData: List[RawRecord]
    def correctExpectedData: List[T]
    def badInputData: List[RawRecord]
    def badExpectedData: List[List[String]]
    def removedInputData: List[RawRecord]
  }

  trait CleanerTestData {
    def correctInputData: List[T]
    def correctExpectedData: List[T]
    def badInputData: List[T]
    def badExpectedData: List[List[String]]
  }

  trait NormalizerTestData {
    def correctInputData: List[T]
    def correctExpectedData: List[Transaction]
    def badInputData: List[T]
    def badExpectedData: List[List[String]]
  }

  def name: String = this.getClass.getSimpleName.replaceAll("""Specs.*$""", "")

  def date(y: Int, m: Int, d: Int): LocalDate = LocalDate.of(y, m, d)

  def applyPipe[I, O](data: List[I])(pipe: VPipe[Pure, I, O]): List[AValidated[O]] =
    Stream
      .emits(data)
      .map(_.valid)
      .through(pipe)
      .toList

  def unwrapValid[A](data: List[AValidated[A]]): List[A] =
    data.flatMap {
      _.fold(_ => List.empty, List(_))
    }

  def unwrapInvalid[A](data: List[AValidated[A]]): List[List[String]] =
    data.flatMap {
      _.fold(x => List(x.toList), _ => List.empty)
    }

  implicit def implProcessor: InputProcessor[T]
  implicit def implCleaner: Cleaner[T]
  implicit def implNormalizer: Normalizer[T]

  def inputProcessorTestData: InputProcessorTestData
  def cleanerTestData: CleanerTestData
  def normalizerTestData: NormalizerTestData
}

// format: off
trait Barclays extends PipelineTestingDefinition[BarclaysTransaction] {
  private[this] val instance = new BarclaysCsvInput()

  implicit val implProcessor: InputProcessor[BarclaysTransaction] = instance
  implicit val implCleaner: Cleaner[BarclaysTransaction] = instance
  implicit val implNormalizer: Normalizer[BarclaysTransaction] = instance

  val inputProcessorTestData: InputProcessorTestData = new InputProcessorTestData {
    val correctInputData: List[RawRecord] = List(
      List("Number", "Date", "Account", "Amount", "Subcategory", "Memo"),
      List(" ", "08/11/2019", "20-32-06 13152170", "6.88", "DIRECTDEP", "DELLELLE           Food 31/10         BGC"),
      List("\t", "08/11/2019", "20-32-06 13152170", "-235.00", "FT", "ANDREW CUMMING         TUNNEL D4          FT"),
      List("\t", "08/11/2019", "20-32-06 13152170", "-23.63", "FT", "C DELLELLE    GROCERY            FT"),
      List("\t", "07/11/2019", "20-32-06 13152170", "-5.70", "PAYMENT", "CRV*EASY BIKE BAR    ON 06 NOV          BCC"),
      List("\t", "05/11/2019", "20-32-06 13152170", "-430.00", "PAYMENT", "HALIFAX CLARITY MA    5353130107545290   BBP"),
      List("\t", "05/11/2019", "20-32-06 13152170", "-4.95", "PAYMENT", "CRV*YOUWORK (1219)     ON 04 NOV          BCC"),
      List("\t", "04/11/2019", "20-32-06 13152170", "-100.00", "FT", "THOR A"),
      List(),
      List(),
    )
    val correctExpectedData: List[BarclaysTransaction] = List(
      BarclaysTransaction(None, date(2019, 11, 8), "20-32-06 13152170", 6.88, "DIRECTDEP", "DELLELLE           Food 31/10         BGC"),
      BarclaysTransaction(None, date(2019, 11, 8), "20-32-06 13152170", -235.0, "FT", "ANDREW CUMMING         TUNNEL D4          FT"),
      BarclaysTransaction(None, date(2019, 11, 8), "20-32-06 13152170", -23.63, "FT", "C DELLELLE    GROCERY            FT"),
      BarclaysTransaction(None, date(2019, 11, 7), "20-32-06 13152170", -5.7, "PAYMENT", "CRV*EASY BIKE BAR    ON 06 NOV          BCC"),
      BarclaysTransaction(None, date(2019, 11, 5), "20-32-06 13152170", -430.0, "PAYMENT", "HALIFAX CLARITY MA    5353130107545290   BBP"),
      BarclaysTransaction(None, date(2019, 11, 5), "20-32-06 13152170", -4.95, "PAYMENT", "CRV*YOUWORK (1219)     ON 04 NOV          BCC"),
      BarclaysTransaction(None, date(2019, 11, 4), "20-32-06 13152170", -100.0, "FT", "THOR A"),
    )

    val badInputData: List[RawRecord] = List(
      List.fill(6)("header"),
      List.fill(6)("text"),
      List("1", "", "20-32-06 13152170", "-100.00", "FT", "THOR A"),
      List("1", null, "20-32-06 13152170", "-100.00", "FT", "THOR A"),
      List("1", "04/11/2019", "20-32-06 13152170", "-100.00", "FT"),
    )
    val badExpectedData: List[List[String]] = List(
      List("java\\.lang\\.NumberFormatException", "java\\.time\\.format\\.DateTimeParseException"),
      List("java\\.time\\.format\\.DateTimeParseException"),
      List("java\\.lang\\.NullPointerException"),
      List("java\\.lang\\.IndexOutOfBoundsException"),
    )

    val removedInputData: List[RawRecord] = List(
      List.fill(6)("header"),
      List.fill(6)(""),
      List.fill(6)(null),
      List.empty,
    )
  }

  val cleanerTestData: CleanerTestData = new CleanerTestData {
    val correctInputData: List[BarclaysTransaction] = inputProcessorTestData.correctExpectedData

    val correctExpectedData: List[BarclaysTransaction] = List(
      BarclaysTransaction(None, date(2019, 11, 8), "20-32-06 13152170", 6.88, "directdep", "dellelle food 31/10 bgc"),
      BarclaysTransaction(None, date(2019, 11, 8), "20-32-06 13152170", -235.0, "ft", "andrew cumming tunnel d4 ft"),
      BarclaysTransaction(None, date(2019, 11, 8), "20-32-06 13152170", -23.63, "ft", "c dellelle grocery ft"),
      BarclaysTransaction(None, date(2019, 11, 7), "20-32-06 13152170", -5.7, "payment", "crv*easy bike bar on 06 nov bcc"),
      BarclaysTransaction(None, date(2019, 11, 5), "20-32-06 13152170", -430.0, "payment", "halifax clarity ma 5353130107545290 bbp"),
      BarclaysTransaction(None, date(2019, 11, 5), "20-32-06 13152170", -4.95, "payment", "crv*youwork (1219) on 04 nov bcc"),
      BarclaysTransaction(None, date(2019, 11, 4), "20-32-06 13152170", -100.0, "ft", "thor a"),
    )

    def badInputData: List[BarclaysTransaction] = List()
    def badExpectedData: List[List[String]] = List()
  }

  val normalizerTestData: NormalizerTestData = new NormalizerTestData {
    def correctInputData: List[BarclaysTransaction] = cleanerTestData.correctExpectedData

    def correctExpectedData: List[Transaction] = List(
      SingleTransaction(java.util.UUID.randomUUID, date(2019, 11, 8), 6.88, "dellelle food 31/10 bgc", BarclaysBank, "", "", ""),
      SingleTransaction(java.util.UUID.randomUUID, date(2019, 11, 8), -235.0, "andrew cumming tunnel d4 ft", BarclaysBank, "", "", ""),
      SingleTransaction(java.util.UUID.randomUUID, date(2019, 11, 8), -23.63, "c dellelle grocery ft", BarclaysBank, "", "", ""),
      SingleTransaction(java.util.UUID.randomUUID, date(2019, 11, 7), -5.7, "crv*easy bike bar on 06 nov bcc", BarclaysBank, "", "", ""),
      SingleTransaction(java.util.UUID.randomUUID, date(2019, 11, 5), -430.0, "halifax clarity ma 5353130107545290 bbp", BarclaysBank, "", "", ""),
      SingleTransaction(java.util.UUID.randomUUID, date(2019, 11, 5), -4.95, "crv*youwork (1219) on 04 nov bcc", BarclaysBank, "", "", ""),
      SingleTransaction(java.util.UUID.randomUUID, date(2019, 11, 4), -100.0, "thor a", BarclaysBank, "", "", ""),
    )

    def badInputData: List[BarclaysTransaction] = List()
    def badExpectedData: List[List[String]] = List()
  }
}

// format: on

/**
 * Defines the tests for all input conversions
 */
@SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
trait NewPipelineSpecsDefs[T <: InputTransaction]
    extends AnyFlatSpec
    with Matchers
    with Inspectors
    with ValidatedMatchers
    with PPrintPrettifier { this: PipelineTestingDefinition[T] =>

  private def inputProcessor = InputProcessor[Pure, T]
  private def cleaner        = Cleaner[Pure, T]
  private def normalizer     = Normalizer[Pure, T]

  //  INPUT PROCESSOR  //

  s"The $name InputProcessor" should "return Valid() items when provided with a valid input" in {
    val result = applyPipe(inputProcessorTestData.correctInputData)(inputProcessor)
    forEvery(result) { computed =>
      computed shouldBe valid
    }
  }

  it should "return correct InputTransactions when provided with a valid input" in {
    val result = unwrapValid(applyPipe(inputProcessorTestData.correctInputData)(inputProcessor))
    result should contain theSameElementsInOrderAs inputProcessorTestData.correctExpectedData
  }

  it should "return Invalid() items when provided with an invalid input" in {
    val result = applyPipe(inputProcessorTestData.badInputData)(inputProcessor)
    forEvery(result) { computed =>
      computed shouldBe invalid
    }
  }

  it should "return a detailed description of the errors" in {
    val result = unwrapInvalid(applyPipe(inputProcessorTestData.badInputData)(inputProcessor))
    result should have size inputProcessorTestData.badExpectedData.length.toLong

    forEvery(result zip inputProcessorTestData.badExpectedData) {
      case (computed, expected) =>
        computed should have size expected.length.toLong
        forEvery(computed zip expected) {
          case (error, regex) => error should include regex regex
        }
    }
  }

  it should "drop specific record values" in {
    val result = unwrapValid(applyPipe(inputProcessorTestData.removedInputData)(inputProcessor))
    result should have size 0
  }

  //  CLEANER  //

  s"The $name Cleaner" should "return Valid() items when provided with a valid input" in {
    val result = applyPipe(cleanerTestData.correctInputData)(cleaner)
    forEvery(result) { computed =>
      computed shouldBe valid
    }
  }

  it should "return correct InputTransactions when provided with a valid input" in {
    val result = unwrapValid(applyPipe(cleanerTestData.correctInputData)(cleaner))
    result should contain theSameElementsInOrderAs cleanerTestData.correctExpectedData
  }

  it should "return Invalid() items when provided with an invalid input" in {
    val result = applyPipe(cleanerTestData.badInputData)(cleaner)
    forEvery(result) { computed =>
      computed shouldBe invalid
    }
  }

  it should "return a detailed description of the errors" in {
    val result = unwrapInvalid(applyPipe(cleanerTestData.badInputData)(cleaner))
    result should have size cleanerTestData.badExpectedData.length.toLong

    forEvery(result zip cleanerTestData.badExpectedData) {
      case (computed, expected) =>
        computed should have size expected.length.toLong
        forEvery(computed zip expected) {
          case (error, regex) => error should include regex regex
        }
    }
  }

  //  NORMALIZER  //

  s"The $name Normalizer" should "return Valid() items when provided with a valid input" in {
    val result = applyPipe(normalizerTestData.correctInputData)(normalizer)
    forEvery(result) { computed =>
      computed shouldBe valid
    }
  }
}

class BarclaysNewPipelineSpecs extends NewPipelineSpecsDefs[BarclaysTransaction] with Barclays
