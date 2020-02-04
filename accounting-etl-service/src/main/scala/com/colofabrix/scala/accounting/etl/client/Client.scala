package com.colofabrix.scala.accounting.etl.client

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.etl.api.Endpoints.ErrorOutput
import com.colofabrix.scala.accounting.etl.ApiPipelineInstances._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.etl.model.Config._
import com.colofabrix.scala.accounting.etl.config._
import com.colofabrix.scala.accounting.etl.pipeline._

object Client {

  /**
   * Returns the list of supported input types
   */
  def listSupportedInputs: IO[Either[ErrorOutput, String]] = IO {
    etlConfig
      .inputTypes
      .map(_.description)
      .mkString(",")
      .asRight[ErrorOutput]
  }

  /**
   * Converts one single input record into one output transaction
   */
  def convertRecord(inputType: InputType, record: String): IO[Either[ErrorOutput, String]] = IO {
    val converted = inputType match {
      case BarclaysInputType => Pipeline.fromCsv[String, BarclaysTransaction](record).head
      case HalifaxInputType  => Pipeline.fromCsv[String, HalifaxTransaction](record).head
      case StarlingInputType => Pipeline.fromCsv[String, StarlingTransaction](record).head
      case AmexInputType     => Pipeline.fromCsv[String, AmexTransaction](record).head
    }
    // TODO: Don't unsafeRun here. Probably using a stream to emit an IO is wrong
    converted.compile.toList.unsafeRunSync.toString.asRight[ErrorOutput]
  }

  /**
   * Converts a list of inputs records into output transactions
   */
  def convertRecords(inputType: InputType, body: String): IO[Either[ErrorOutput, String]] = IO {
    val record: RawInput[IO] = fs2.Stream.emit(List(body))
    val converted = inputType match {
      case BarclaysInputType => Pipeline.fromStream[IO, BarclaysTransaction](record)
      case HalifaxInputType  => Pipeline.fromStream[IO, HalifaxTransaction](record)
      case StarlingInputType => Pipeline.fromStream[IO, StarlingTransaction](record)
      case AmexInputType     => Pipeline.fromStream[IO, AmexTransaction](record)
    }
    // TODO: Don't unsafeRun here. Probably using a stream to emit an IO is wrong
    converted.compile.toList.unsafeRunSync.toString.asRight[ErrorOutput]
  }

}
