package com.colofabrix.scala.accounting.etl.client

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.etl.ApiPipelineInstances._
import com.colofabrix.scala.accounting.etl.config._
import com.colofabrix.scala.accounting.etl.conversion.CsvReader
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.model.Config._
import com.colofabrix.scala.accounting.utils.validation._
import com.colofabrix.scala.accounting.etl.model.Api.EtlApiError

object Client {

  /**
   * Returns the list of supported input types
   */
  def listSupportedInputs: IO[Either[EtlApiError, String]] = IO {
    etlConfig
      .inputTypes
      .map(_.description)
      .mkString(",")
      .asRight[EtlApiError]
  }

  /**
   * Converts one single input record into one output transaction
   */
  def convertRecord(inputType: InputType, record: String): IO[Either[EtlApiError, String]] = IO {
    // TODO: Don't unsafeRun here. Probably using a stream to emit an IO is wrong
    new CsvReader(record)
      .read
      .through(pipelineForType(inputType))
      .compile
      .toList
      .unsafeRunSync
      .toString
      .asRight[EtlApiError]
  }

  /**
   * Converts a list of inputs records into output transactions
   */
  def convertRecords(inputType: InputType, body: String): IO[Either[EtlApiError, String]] = IO {
    // TODO: Don't unsafeRun here. Probably using a stream to emit an IO is wrong
    val record: RawInput[IO] = fs2.Stream.emit(List(body))
    record
      .map(_.aValid)
      .through(pipelineForType(inputType))
      .compile
      .toList
      .unsafeRunSync
      .toString
      .asRight[EtlApiError]
  }

}
