package com.colofabrix.scala.accounting.etl.pipeline.data

import com.colofabrix.scala.accounting.etl.inputs._
import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.etl.pipeline._
import com.colofabrix.scala.accounting.etl.pipeline.definitions.{ PipelineDefinitions, PipelineStep }
import com.colofabrix.scala.accounting.model.BankType._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation.streams._
import eu.timepit.refined.auto._
import fs2._
import shapeless._

/**
 * Pipeline data definition for Barclays
 */
@SuppressWarnings(Array("org.wartremover.warts.Null"))
trait HalifaxTestData extends PipelineDefinitions[HalifaxTransaction] {
  // format: off
  private[this] val uuid = java.util.UUID.randomUUID

  private[this] val instance = new HalifaxCsvInput()

  //  INPUT PROCESSING  //

  private[this] val processingStep: PipelineStep[RawRecord, HalifaxTransaction] = new PipelineStep[RawRecord, HalifaxTransaction] {
    def name: String = "InputProcessor"

    def operation: VPipe[Pure, RawRecord, HalifaxTransaction] = {
      InputProcessor[Pure, HalifaxTransaction](instance)
    }

    def inputData: List[RawRecord] = List(
      List("Date", "Date entered", "Reference", "Description", "Amount"),
      List("07/11/2019", "07/11/2019", "99630930", "INTEREST ", "0.65"),
      List("04/11/2019", "05/11/2019", "68125600", "PAYMENT RECEIVED - THAN ", "-430.00"),
      List("01/11/2019", "01/11/2019", "99691550", "DIRECT DEBIT PAYMENT -  ", "-26.32"),
      List("19/10/2019", "22/10/2019", "10210344", "IRISH DRUID            ", "12.55"),
      List("19/10/2019", "22/10/2019", "10209649", "COLLINA RISTORANTE        ", "21.64"),
      List("19/10/2019", "22/10/2019", "10209091", "BOOKLET LIBRERIE        ", "12.12"),
      List("19/10/2019", "21/10/2019", "10224975", "IPER CONAD              ", "31.17"),
      List(),
    )

    def expectedOutputData: List[HalifaxTransaction] = List(
      HalifaxTransaction(date(2019, 11,  7), date(2019, 11,  7), "99630930", "INTEREST ", -0.65),
      HalifaxTransaction(date(2019, 11,  4), date(2019, 11,  5), "68125600", "PAYMENT RECEIVED - THAN ", 430.0),
      HalifaxTransaction(date(2019, 11,  1), date(2019, 11,  1), "99691550", "DIRECT DEBIT PAYMENT -  ", 26.32),
      HalifaxTransaction(date(2019, 10, 19), date(2019, 10, 22), "10210344", "IRISH DRUID            ", -12.55),
      HalifaxTransaction(date(2019, 10, 19), date(2019, 10, 22), "10209649", "COLLINA RISTORANTE        ", -21.64),
      HalifaxTransaction(date(2019, 10, 19), date(2019, 10, 22), "10209091", "BOOKLET LIBRERIE        ", -12.12),
      HalifaxTransaction(date(2019, 10, 19), date(2019, 10, 21), "10224975", "IPER CONAD              ", -31.17),
    )

    def malformedData: List[RawRecord] = List(
      List.fill(5)("header"),
      List.fill(5)("text"),
      List("19/10/2019", "", "10224975", "IPER CONAD", "31.17"),
      List("19/10/2019", null, "10224975", "IPER CONAD", "31.17"),
      List("19/10/2019", "21/10/2019", "10224975", "IPER CONAD"),
    )

    def expectedErrorMatches: List[List[String]] = List(
      List(
        "java\\.lang\\.NumberFormatException",
        "java\\.time\\.format\\.DateTimeParseException",
        "java\\.time\\.format\\.DateTimeParseException",
      ),
      List("java\\.time\\.format\\.DateTimeParseException"),
      List("java\\.lang\\.NullPointerException"),
      List("java\\.lang\\.IndexOutOfBoundsException"),
    )

    def removedData: List[RawRecord] = List(
      List.fill(6)("header"),
      List.fill(6)(""),
      List.fill(6)(null),
      List.empty,
    )
  }

  //  CLEANING  //

  private[this] val cleaningStep: PipelineStep[HalifaxTransaction, HalifaxTransaction] = new PipelineStep[HalifaxTransaction, HalifaxTransaction] {
    def name: String = "Cleaner"

    def operation: VPipe[Pure, HalifaxTransaction, HalifaxTransaction] = {
      Cleaner[Pure, HalifaxTransaction](instance)
    }

    def inputData: List[HalifaxTransaction] = processingStep.expectedOutputData

    def expectedOutputData: List[HalifaxTransaction] = List(
      HalifaxTransaction(date(2019, 11,  7), date(2019, 11,  7), "99630930", "interest", -0.65),
      HalifaxTransaction(date(2019, 11,  4), date(2019, 11,  5), "68125600", "payment received - than", 430.0),
      HalifaxTransaction(date(2019, 11,  1), date(2019, 11,  1), "99691550", "direct debit payment -", 26.32),
      HalifaxTransaction(date(2019, 10, 19), date(2019, 10, 22), "10210344", "irish druid", -12.55),
      HalifaxTransaction(date(2019, 10, 19), date(2019, 10, 22), "10209649", "collina ristorante", -21.64),
      HalifaxTransaction(date(2019, 10, 19), date(2019, 10, 22), "10209091", "booklet librerie", -12.12),
      HalifaxTransaction(date(2019, 10, 19), date(2019, 10, 21), "10224975", "iper conad", -31.17),
    )

    def malformedData: List[HalifaxTransaction] = List(
      HalifaxTransaction(date(2019, 11,  4), date(2019, 11,  5), "68125600", "    ", 430.0),
    )

    def expectedErrorMatches: List[List[String]] = List(
      List("Predicate.*fail"),
    )

    def removedData: List[HalifaxTransaction] = List.empty
  }

  //  NORMALIZING  //

  private[this] val normalizingStep: PipelineStep[HalifaxTransaction, Transaction] = new PipelineStep[HalifaxTransaction, Transaction] {
    def name: String = "Normalizer"

    def operation: VPipe[Pure, HalifaxTransaction, Transaction] = {
      Normalizer[Pure, HalifaxTransaction](instance) andThen {
        _.map(_.map(_.copy(id = uuid)))
      }
    }

    def inputData: List[HalifaxTransaction] = cleaningStep.expectedOutputData

    def expectedOutputData: List[Transaction] = List(
      SingleTransaction(uuid, date(2019, 11,  7), -0.65, "interest", HalifaxBank, "", "", ""),
      SingleTransaction(uuid, date(2019, 11,  4), 430.0, "payment received - than", HalifaxBank, "", "", ""),
      SingleTransaction(uuid, date(2019, 11,  1), 26.32, "direct debit payment -", HalifaxBank, "", "", ""),
      SingleTransaction(uuid, date(2019, 10, 19), -12.55, "irish druid", HalifaxBank, "", "", ""),
      SingleTransaction(uuid, date(2019, 10, 19), -21.64, "collina ristorante", HalifaxBank, "", "", ""),
      SingleTransaction(uuid, date(2019, 10, 19), -12.12, "booklet librerie", HalifaxBank, "", "", ""),
      SingleTransaction(uuid, date(2019, 10, 19), -31.17, "iper conad", HalifaxBank, "", "", ""),
    )

    def malformedData: List[HalifaxTransaction] = List.empty
    def expectedErrorMatches: List[List[String]] = List.empty
    def removedData: List[HalifaxTransaction] = List.empty
  }

  //  FULL PIPELINE  //

  private[this] val fullPipeline: PipelineStep[RawRecord, Transaction] = new PipelineStep[RawRecord, Transaction] {
    def name: String = "Full Pipeline"

    def operation: VPipe[Pure, RawRecord, Transaction] = {
      val processing = InputProcessor[Pure, HalifaxTransaction](instance)
      val cleaning = Cleaner[Pure, HalifaxTransaction](instance)
      val normalizing = Normalizer[Pure, HalifaxTransaction](instance)
      val uniformUUID: VPipe[Pure, SingleTransaction, SingleTransaction] = {
        _.map(_.map(_.copy(id = uuid)))
      }

      processing andThen cleaning andThen normalizing andThen uniformUUID
    }

    def inputData: List[RawRecord] = processingStep.inputData
    def expectedOutputData: List[Transaction] = normalizingStep.expectedOutputData
    def malformedData: List[RawRecord] = List.empty
    def expectedErrorMatches: List[List[String]] = List.empty
    def removedData: List[RawRecord] = List.empty
  }

  def name: String = "Halifax"

  val pipelineSteps = processingStep :: cleaningStep :: normalizingStep :: fullPipeline :: HNil
  // format: on
}
