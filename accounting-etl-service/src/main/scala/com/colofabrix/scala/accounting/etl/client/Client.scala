package com.colofabrix.scala.accounting.etl.client

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.etl.api.Endpoints.ErrorOutput
import com.colofabrix.scala.accounting.etl.ApiPipelineInstances._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.pipeline._
import com.colofabrix.scala.accounting.model._

object Client {

  /**
   * Returns the list of supported input types
   */
  def listSupportedInputs: IO[Either[ErrorOutput, String]] = IO {
    InputType
      .all
      .mkString(",")
      .asRight[ErrorOutput]
  }

  /**
   * Converts one single input record into one output transaction
   */
  def convertRecord(inputType: InputType, body: String): IO[Either[ErrorOutput, String]] = IO {
    val converted = inputType match {
      case BarclaysInputType => Pipeline.fromCsv[String, BarclaysTransaction](body).head
      case HalifaxInputType  => Pipeline.fromCsv[String, HalifaxTransaction](body).head
      case StarlingInputType => Pipeline.fromCsv[String, StarlingTransaction](body).head
      case AmexInputType     => Pipeline.fromCsv[String, AmexTransaction](body).head
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
