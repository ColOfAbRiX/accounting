package com.colofabrix.scala.accounting.etl.client

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.etl.ApiPipelineInstances._
import com.colofabrix.scala.accounting.etl.config._
import com.colofabrix.scala.accounting.etl.conversion.CsvReader
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.model.Api._
import com.colofabrix.scala.accounting.etl.model.Config._
import com.colofabrix.scala.accounting.model.Transaction
import com.colofabrix.scala.accounting.utils.validation._

object Client {

  implicit class ApiValidated[A](validated: AValidated[A]) {
    def toErrorInfo: Either[ErrorInfo, A] = {
      validated
        .leftMap(_.toList)
        .leftMap(ValidationErrors)
        .toEither
    }
  }

  /**
   * Returns the list of supported input types
   */
  def listSupportedInputs: IO[Either[ErrorInfo, Set[InputType]]] = IO.delay {
    etlConfig.inputTypes.asRight
  }

  /**
   * Converts one single input record into one output transaction
   */
  @SuppressWarnings(Array("org.wartremover.warts.All"))
  def convertRecord(inputType: InputType, record: String): IO[Either[ErrorInfo, Transaction]] = {
    new CsvReader(record)
      .read
      .through(pipelineForType(inputType))
      .map(_.toErrorInfo)
      .compile
      .toList
      .map(_.head)
  }

  /**
   * Converts a list of inputs records into output transactions
   */
  def convertRecords(inputType: InputType, body: String): IO[Either[ErrorInfo, String]] = IO.delay {
    // TODO: Don't unsafeRun here. Probably using a stream to emit an IO is wrong
    val record: RawInput[IO] = fs2.Stream.emit(List(body))
    record
      .map(_.aValid)
      .through(pipelineForType(inputType))
      .compile
      .toList
      .unsafeRunSync
      .toString
      .asRight[ErrorInfo]
  }

}
