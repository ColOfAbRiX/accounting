package com.colofabrix.scala.accounting.etl.client

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.etl.api.Endpoints.ErrorOutput
import com.colofabrix.scala.accounting.etl.ApiPipelineInstances._
import com.colofabrix.scala.accounting.etl.config._
import com.colofabrix.scala.accounting.etl.CsvReader
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.model.Config._
import com.colofabrix.scala.accounting.utils.validation._

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
    // TODO: Don't unsafeRun here. Probably using a stream to emit an IO is wrong
    new CsvReader(record)
      .read
      .through(pipelineForType(inputType))
      .compile
      .toList
      .unsafeRunSync
      .toString
      .asRight[ErrorOutput]
  }

  /**
   * Converts a list of inputs records into output transactions
   */
  def convertRecords(inputType: InputType, body: String): IO[Either[ErrorOutput, String]] = IO {
    // TODO: Don't unsafeRun here. Probably using a stream to emit an IO is wrong
    val record: RawInput[IO] = fs2.Stream.emit(List(body))
    record
      .map(_.aValid)
      .through(pipelineForType(inputType))
      .compile
      .toList
      .unsafeRunSync
      .toString
      .asRight[ErrorOutput]
  }

}
