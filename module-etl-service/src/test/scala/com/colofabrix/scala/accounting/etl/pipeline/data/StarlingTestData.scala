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
trait StarlingTestData extends PipelineDefinitions[StarlingTransaction] {
  // format: off
  private[this] val uuid = java.util.UUID.randomUUID

  private[this] val instance = new StarlingCsvInput()

  //  INPUT PROCESSING  //

  private[this] val processingStep: PipelineStep[RawRecord, StarlingTransaction] = new PipelineStep[RawRecord, StarlingTransaction] {
    def name: String = "InputProcessor"

    def operation: VPipe[Pure, RawRecord, StarlingTransaction] = {
      InputProcessor[Pure, StarlingTransaction](instance)
    }

    def inputData: List[RawRecord] = List(
      List("Date", "Counter Party", "Reference", "Type", "Amount (GBP)", "Balance (GBP)"),
      List("", "Opening Balance", "", "", "", "0.00"),
      List("01/03/2019", "COLUMN F", "TOP UP STARLING", "FASTER PAYMENT", "100.00", "100.00"),
      List("04/03/2019", "Butler Brewery C Chelmsford", "IZ *BUTLER BREWERY C Chelmsford    GBR", "CONTACTLESS", "-8.00", "92.00"),
      List("04/03/2019", "Sainsbury's", "SAINSBURYS SACAT 0768  CHELMSFORD    GBR", "CONTACTLESS", "-3.70", "88.30"),
      List(),
    )

    def expectedOutputData: List[StarlingTransaction] = List(
      StarlingTransaction(date(2019, 3, 1), "COLUMN F", "TOP UP STARLING", "FASTER PAYMENT", 100.0, 100.0),
      StarlingTransaction(date(2019, 3, 4), "Butler Brewery C Chelmsford", "IZ *BUTLER BREWERY C Chelmsford    GBR", "CONTACTLESS", -8.0, 92.0),
      StarlingTransaction(date(2019, 3, 4), "Sainsbury's", "SAINSBURYS SACAT 0768  CHELMSFORD    GBR", "CONTACTLESS", -3.7, 88.3),
    )

    def malformedData: List[RawRecord] = List(
      List.fill(6)("header"),
      List.fill(6)("text"),
      List("", "Sainsbury's", "SAINSBURYS SACAT", "CONTACTLESS", "-3.70", "88.30"),
      List(null, "Sainsbury's", "SAINSBURYS SACAT", "CONTACTLESS", "-3.70", "88.30"),
      List("04/03/2019", "Sainsbury's", "SAINSBURYS SACAT", "CONTACTLESS", "-3.70"),
    )

    def expectedErrorMatches: List[List[String]] = List(
      List(
        "java\\.lang\\.NumberFormatException",
        "java\\.lang\\.NumberFormatException",
        "java\\.time\\.format\\.DateTimeParseException",
      ),
      List("java\\.time\\.format\\.DateTimeParseException"),
      List("java\\.lang\\.NullPointerException"),
      List("java\\.lang\\.IndexOutOfBoundsException"),
    )

    def removedData: List[RawRecord] = List(
      List.fill(6)("header"),
      List.fill(6)("opening balance"),
      List.fill(6)(""),
      List.fill(6)(null),
      List.empty,
    )
  }

  //  CLEANING  //

  private[this] val cleaningStep: PipelineStep[StarlingTransaction, StarlingTransaction] = new PipelineStep[StarlingTransaction, StarlingTransaction] {
    def name: String = "Cleaner"

    def operation: VPipe[Pure, StarlingTransaction, StarlingTransaction] = {
      Cleaner[Pure, StarlingTransaction](instance)
    }

    def inputData: List[StarlingTransaction] = processingStep.expectedOutputData

    def expectedOutputData: List[StarlingTransaction] = List(
      StarlingTransaction(date(2019, 3, 1), "column f", "top up starling", "faster payment", 100.0, 100.0),
      StarlingTransaction(date(2019, 3, 4), "butler brewery c chelmsford", "iz *butler brewery c chelmsford gbr", "contactless", -8.0, 92.0),
      StarlingTransaction(date(2019, 3, 4), "sainsbury's", "sainsburys sacat 0768 chelmsford gbr", "contactless", -3.7, 88.3),
    )

    def malformedData: List[StarlingTransaction] = List(
      StarlingTransaction(date(2019, 3, 4), "butler brewery c chelmsford", "    ", "contactless", -8.0, 92.0),
    )

    def expectedErrorMatches: List[List[String]] = List(
      List("Predicate.*fail"),
    )

    def removedData: List[StarlingTransaction] = List.empty
  }

  //  NORMALIZING  //

  private[this] val normalizingStep: PipelineStep[StarlingTransaction, Transaction] = new PipelineStep[StarlingTransaction, Transaction] {
    def name: String = "Normalizer"

    def operation: VPipe[Pure, StarlingTransaction, Transaction] = {
      Normalizer[Pure, StarlingTransaction](instance) andThen {
        _.map(_.map(_.copy(id = uuid)))
      }
    }

    def inputData: List[StarlingTransaction] = cleaningStep.expectedOutputData

    def expectedOutputData: List[Transaction] = List(
      SingleTransaction(uuid, date(2019, 3, 1), 100.0, "top up starling", StarlingBank, "", "", ""),
      SingleTransaction(uuid, date(2019, 3, 4), -8.0, "iz *butler brewery c chelmsford gbr", StarlingBank, "", "", ""),
      SingleTransaction(uuid, date(2019, 3, 4), -3.7, "sainsburys sacat 0768 chelmsford gbr", StarlingBank, "", "", ""),
    )

    def malformedData: List[StarlingTransaction] = List.empty

    def expectedErrorMatches: List[List[String]] = List.empty

    def removedData: List[StarlingTransaction] = List.empty
  }

  //  FULL PIPELINE  //

  private[this] val fullPipeline: PipelineStep[RawRecord, Transaction] = new PipelineStep[RawRecord, Transaction] {
    def name: String = "Full Pipeline"

    def operation: VPipe[Pure, RawRecord, Transaction] = {
      val processing = InputProcessor[Pure, StarlingTransaction](instance)
      val cleaning = Cleaner[Pure, StarlingTransaction](instance)
      val normalizing = Normalizer[Pure, StarlingTransaction](instance)
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

  def name: String = "Starling"

  val pipelineSteps = processingStep :: cleaningStep :: normalizingStep :: fullPipeline :: HNil
  // format: on
}
