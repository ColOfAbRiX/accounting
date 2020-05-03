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
trait BarclaysTestData extends PipelineDefinitions[BarclaysTransaction] {
  // format: off
  private[this] val uuid = java.util.UUID.randomUUID

  private[this] val instance = new BarclaysCsvInput()

  //  INPUT PROCESSING  //

  private[this] val processingStep: PipelineStep[RawRecord, BarclaysTransaction] = new PipelineStep[RawRecord, BarclaysTransaction] {
    def name: String = "InputProcessor"

    def operation: VPipe[Pure, RawRecord, BarclaysTransaction] = {
      InputProcessor[Pure, BarclaysTransaction](instance)
    }

    def inputData: List[RawRecord] = List(
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

    def expectedOutputData: List[BarclaysTransaction] = List(
      BarclaysTransaction(None, date(2019, 11, 8), "20-32-06 13152170", 6.88, "DIRECTDEP", "DELLELLE           Food 31/10         BGC"),
      BarclaysTransaction(None, date(2019, 11, 8), "20-32-06 13152170", -235.0, "FT", "ANDREW CUMMING         TUNNEL D4          FT"),
      BarclaysTransaction(None, date(2019, 11, 8), "20-32-06 13152170", -23.63, "FT", "C DELLELLE    GROCERY            FT"),
      BarclaysTransaction(None, date(2019, 11, 7), "20-32-06 13152170", -5.7, "PAYMENT", "CRV*EASY BIKE BAR    ON 06 NOV          BCC"),
      BarclaysTransaction(None, date(2019, 11, 5), "20-32-06 13152170", -430.0, "PAYMENT", "HALIFAX CLARITY MA    5353130107545290   BBP"),
      BarclaysTransaction(None, date(2019, 11, 5), "20-32-06 13152170", -4.95, "PAYMENT", "CRV*YOUWORK (1219)     ON 04 NOV          BCC"),
      BarclaysTransaction(None, date(2019, 11, 4), "20-32-06 13152170", -100.0, "FT", "THOR A"),
    )

    def malformedData: List[RawRecord] = List(
      List.fill(6)("header"),
      List.fill(6)("text"),
      List("1", "", "20-32-06 13152170", "-100.00", "FT", "THOR A"),
      List("1", null, "20-32-06 13152170", "-100.00", "FT", "THOR A"),
      List("1", "04/11/2019", "20-32-06 13152170", "-100.00", "FT"),
    )

    def expectedErrorMatches: List[List[String]] = List(
      List("java\\.lang\\.NumberFormatException", "java\\.time\\.format\\.DateTimeParseException"),
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

  private[this] val cleaningStep: PipelineStep[BarclaysTransaction, BarclaysTransaction] = new PipelineStep[BarclaysTransaction, BarclaysTransaction] {
    def name: String = "Cleaner"

    def operation: VPipe[Pure, BarclaysTransaction, BarclaysTransaction] =  {
      Cleaner[Pure, BarclaysTransaction](instance)
    }

    def inputData: List[BarclaysTransaction] = processingStep.expectedOutputData

    def expectedOutputData: List[BarclaysTransaction] = List(
      BarclaysTransaction(None, date(2019, 11, 8), "20-32-06 13152170", 6.88, "directdep", "dellelle food 31/10 bgc"),
      BarclaysTransaction(None, date(2019, 11, 8), "20-32-06 13152170", -235.0, "ft", "andrew cumming tunnel d4 ft"),
      BarclaysTransaction(None, date(2019, 11, 8), "20-32-06 13152170", -23.63, "ft", "c dellelle grocery ft"),
      BarclaysTransaction(None, date(2019, 11, 7), "20-32-06 13152170", -5.7, "payment", "crv*easy bike bar on 06 nov bcc"),
      BarclaysTransaction(None, date(2019, 11, 5), "20-32-06 13152170", -430.0, "payment", "halifax clarity ma 5353130107545290 bbp"),
      BarclaysTransaction(None, date(2019, 11, 5), "20-32-06 13152170", -4.95, "payment", "crv*youwork (1219) on 04 nov bcc"),
      BarclaysTransaction(None, date(2019, 11, 4), "20-32-06 13152170", -100.0, "ft", "thor a"),
    )

    def malformedData: List[BarclaysTransaction] = List(
      BarclaysTransaction(None, date(2019, 11, 8), "20-32-06 13152170", -235.0, "ft", "    "),
    )

    def expectedErrorMatches: List[List[String]] = List(
      List("Predicate.*fail"),
    )

    def removedData: List[BarclaysTransaction] = List.empty
  }

  //  NORMALIZING  //

  private[this] val normalizingStep: PipelineStep[BarclaysTransaction, Transaction] = new PipelineStep[BarclaysTransaction, Transaction] {
    def name: String = "Normalizer"

    def operation: VPipe[Pure, BarclaysTransaction, Transaction] =  {
      Normalizer[Pure, BarclaysTransaction](instance) andThen {
        _.map(_.map(_.copy(id = uuid)))
      }
    }

    def inputData: List[BarclaysTransaction] = cleaningStep.expectedOutputData

    def expectedOutputData: List[Transaction] = List(
      SingleTransaction(uuid, date(2019, 11, 8), 6.88, "dellelle food 31/10 bgc", BarclaysBank, "", "", ""),
      SingleTransaction(uuid, date(2019, 11, 8), -235.0, "andrew cumming tunnel d4 ft", BarclaysBank, "", "", ""),
      SingleTransaction(uuid, date(2019, 11, 8), -23.63, "c dellelle grocery ft", BarclaysBank, "", "", ""),
      SingleTransaction(uuid, date(2019, 11, 7), -5.7, "crv*easy bike bar on 06 nov bcc", BarclaysBank, "", "", ""),
      SingleTransaction(uuid, date(2019, 11, 5), -430.0, "halifax clarity ma 5353130107545290 bbp", BarclaysBank, "", "", ""),
      SingleTransaction(uuid, date(2019, 11, 5), -4.95, "crv*youwork (1219) on 04 nov bcc", BarclaysBank, "", "", ""),
      SingleTransaction(uuid, date(2019, 11, 4), -100.0, "thor a", BarclaysBank, "", "", ""),
    )

    def malformedData: List[BarclaysTransaction] = List.empty

    def expectedErrorMatches: List[List[String]] = List.empty

    def removedData: List[BarclaysTransaction] = List.empty
  }

  //  FULL PIPELINE  //

  private[this] val fullPipeline: PipelineStep[RawRecord, Transaction] = new PipelineStep[RawRecord, Transaction] {
    def name: String = "Full Pipeline"

    def operation: VPipe[Pure, RawRecord, Transaction] = {
      val processing = InputProcessor[Pure, BarclaysTransaction](instance)
      val cleaning = Cleaner[Pure, BarclaysTransaction](instance)
      val normalizing = Normalizer[Pure, BarclaysTransaction](instance)
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

  def name: String = "Barclays"

  val pipelineSteps = processingStep :: cleaningStep :: normalizingStep :: fullPipeline :: HNil
  // format: on
}
