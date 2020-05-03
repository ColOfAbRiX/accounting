package com.colofabrix.scala.accounting.etl.pipeline.data

import com.colofabrix.scala.accounting.etl.inputs._
import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.etl.pipeline._
import com.colofabrix.scala.accounting.etl.pipeline.definitions.{ PipelineDefinitions, PipelineStep }
import com.colofabrix.scala.accounting.model.BankType.AmexBank
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation.streams._
import eu.timepit.refined.auto._
import fs2._
import shapeless._

/**
 * Pipeline data definition for Barclays
 */
@SuppressWarnings(Array("org.wartremover.warts.Null"))
trait AmexTestData extends PipelineDefinitions[AmexTransaction] {
  // format: off
  private[this] val uuid = java.util.UUID.randomUUID

  private[this] val instance = new AmexCsvInput()

  //  INPUT PROCESSING  //

  private[this] val processingStep: PipelineStep[RawRecord, AmexTransaction] = new PipelineStep[RawRecord, AmexTransaction] {
    def name: String = "InputProcessor"

    def operation: VPipe[Pure, RawRecord, AmexTransaction] = {
      InputProcessor[Pure, AmexTransaction](instance)
    }

    def inputData: List[RawRecord] = List(
      List("21/10/2019", "Reference: AT192160041000011301953", " 1.50", "YGA TRAVEL CHARGE YGA.GOV.NL/CP", " Process Date 22/10/2019"),
      List("23/10/2019", "Reference: AT192170042000011328509", " 3.30", "MARKS & SPENCER SOUT", "RETAIL GOODS Process Date 24/10/2019  RETAIL GOODS"),
      List("25/10/2019", "Reference: AT192190034000011385110", " 16.76", "BEERGERMANHALL", " Process Date 25/10/2019"),
      List("25/10/2019", "Reference: AT192190034000011350881", " 6.80", "YGA TRAVEL CHARGE YGA.GOV.NL/CP", " Process Date 25/10/2019"),
      List("26/10/2019", "Reference: AT193100034000011249397", " -7.50", "IZ *PGKU LIMITED BEWDLEY", " Process Date 26/10/2019"),
      List("26/10/2019", "Reference: AT193110050000011236226", " 35.23", "TRAINLINE.COM", " Process Date 27/10/2019"),
      List("27/10/2019", "Reference: AT193110060000011238382", " 7.75", "THE LORD MORRIS", " Process Date 27/10/2019"),
      List(),
    )

    def expectedOutputData: List[AmexTransaction] = List(
      AmexTransaction(date(2019, 10, 21), "Reference: AT192160041000011301953", -1.5, "YGA TRAVEL CHARGE YGA.GOV.NL/CP", " Process Date 22/10/2019"),
      AmexTransaction(date(2019, 10, 23), "Reference: AT192170042000011328509", -3.3, "MARKS & SPENCER SOUT", "RETAIL GOODS Process Date 24/10/2019  RETAIL GOODS"),
      AmexTransaction(date(2019, 10, 25), "Reference: AT192190034000011385110", -16.76, "BEERGERMANHALL", " Process Date 25/10/2019"),
      AmexTransaction(date(2019, 10, 25), "Reference: AT192190034000011350881", -6.8, "YGA TRAVEL CHARGE YGA.GOV.NL/CP", " Process Date 25/10/2019"),
      AmexTransaction(date(2019, 10, 26), "Reference: AT193100034000011249397", 7.5, "IZ *PGKU LIMITED BEWDLEY", " Process Date 26/10/2019"),
      AmexTransaction(date(2019, 10, 26), "Reference: AT193110050000011236226", -35.23, "TRAINLINE.COM", " Process Date 27/10/2019"),
      AmexTransaction(date(2019, 10, 27), "Reference: AT193110060000011238382", -7.75, "THE LORD MORRIS", " Process Date 27/10/2019"),
    )

    def malformedData: List[RawRecord] = List(
      List.fill(5)("text"),
      List("27/10/2019", "Reference", "", "THE LORD", " Process 27/10/2019"),
      List("27/10/2019", "Reference", null, "THE LORD", " Process 27/10/2019"),
      List("27/10/2019", "Reference", " 7.75", "THE LORD"),
    )

    def expectedErrorMatches: List[List[String]] = List(
      List("java\\.lang\\.NumberFormatException", "java\\.time\\.format\\.DateTimeParseException"),
      List("java\\.lang\\.NumberFormatException"),
      List("java\\.lang\\.NullPointerException"),
      List("java\\.lang\\.IndexOutOfBoundsException"),
    )

    def removedData: List[RawRecord] = List(
      List.fill(6)(""),
      List.fill(6)(null),
      List.empty,
    )
  }

  //  CLEANING  //

  private[this] val cleaningStep: PipelineStep[AmexTransaction, AmexTransaction] = new PipelineStep[AmexTransaction, AmexTransaction] {
    def name: String = "Cleaner"

    def operation: VPipe[Pure, AmexTransaction, AmexTransaction] = {
      Cleaner[Pure, AmexTransaction](instance)
    }

    def inputData: List[AmexTransaction] = processingStep.expectedOutputData

    def expectedOutputData: List[AmexTransaction] = List(
      AmexTransaction(date(2019, 10, 21), "reference: at192160041000011301953", -1.5, "yga travel charge yga.gov.nl/cp", "process date 22/10/2019"),
      AmexTransaction(date(2019, 10, 23), "reference: at192170042000011328509", -3.3, "marks & spencer sout", "retail goods process date 24/10/2019 retail goods"),
      AmexTransaction(date(2019, 10, 25), "reference: at192190034000011385110", -16.76, "beergermanhall", "process date 25/10/2019"),
      AmexTransaction(date(2019, 10, 25), "reference: at192190034000011350881", -6.8, "yga travel charge yga.gov.nl/cp", "process date 25/10/2019"),
      AmexTransaction(date(2019, 10, 26), "reference: at193100034000011249397", 7.5, "iz *pgku limited bewdley", "process date 26/10/2019"),
      AmexTransaction(date(2019, 10, 26), "reference: at193110050000011236226", -35.23, "trainline.com", "process date 27/10/2019"),
      AmexTransaction(date(2019, 10, 27), "reference: at193110060000011238382", -7.75, "the lord morris", "process date 27/10/2019"),
    )

    def malformedData: List[AmexTransaction] = List(
      AmexTransaction(date(2019, 10, 21), "Reference: AT192160041000011301953", -1.5, "    ", " Process Date 22/10/2019"),
    )

    def expectedErrorMatches: List[List[String]] = List(
      List("Predicate.*fail"),
    )

    def removedData: List[AmexTransaction] = List.empty
  }

  //  NORMALIZING  //

  private[this] val normalizingStep: PipelineStep[AmexTransaction, Transaction] = new PipelineStep[AmexTransaction, Transaction] {
    def name: String = "Normalizer"

    def operation: VPipe[Pure, AmexTransaction, Transaction] = {
      Normalizer[Pure, AmexTransaction](instance) andThen {
        _.map(_.map(_.copy(id = uuid)))
      }
    }

    def inputData: List[AmexTransaction] = cleaningStep.expectedOutputData

    def expectedOutputData: List[Transaction] = List(
      SingleTransaction(uuid, date(2019, 10, 21), -1.5, "yga travel charge yga.gov.nl/cp", AmexBank, "", "", ""),
      SingleTransaction(uuid, date(2019, 10, 23), -3.3, "marks & spencer sout", AmexBank, "", "", ""),
      SingleTransaction(uuid, date(2019, 10, 25), -16.76, "beergermanhall", AmexBank, "", "", ""),
      SingleTransaction(uuid, date(2019, 10, 25), -6.8, "yga travel charge yga.gov.nl/cp", AmexBank, "", "", ""),
      SingleTransaction(uuid, date(2019, 10, 26), 7.5, "iz *pgku limited bewdley", AmexBank, "", "", ""),
      SingleTransaction(uuid, date(2019, 10, 26), -35.23, "trainline.com", AmexBank, "", "", ""),
      SingleTransaction(uuid, date(2019, 10, 27), -7.75, "the lord morris", AmexBank, "", "", ""),
    )

    def malformedData: List[AmexTransaction] = List.empty

    def expectedErrorMatches: List[List[String]] = List.empty

    def removedData: List[AmexTransaction] = List.empty
  }

  //  FULL PIPELINE  //

  private[this] val fullPipeline: PipelineStep[RawRecord, Transaction] = new PipelineStep[RawRecord, Transaction] {
    def name: String = "Full Pipeline"

    def operation: VPipe[Pure, RawRecord, Transaction] = {
      val processing = InputProcessor[Pure, AmexTransaction](instance)
      val cleaning = Cleaner[Pure, AmexTransaction](instance)
      val normalizing = Normalizer[Pure, AmexTransaction](instance)
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

  def name: String = "Amex"

  val pipelineSteps = processingStep :: cleaningStep :: normalizingStep :: fullPipeline :: HNil
  // format: on
}
