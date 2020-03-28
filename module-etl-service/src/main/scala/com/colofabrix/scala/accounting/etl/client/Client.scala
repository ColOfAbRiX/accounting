package com.colofabrix.scala.accounting.etl.client

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.etl.config._
import com.colofabrix.scala.accounting.etl.conversion.CsvReader
import com.colofabrix.scala.accounting.etl.model.Api._
import com.colofabrix.scala.accounting.etl.model.Config._
import com.colofabrix.scala.accounting.etl.pipeline.ApiPipelineInstances._
import com.colofabrix.scala.accounting.model.Transaction
import com.colofabrix.scala.accounting.utils.concurrency._
import com.colofabrix.scala.accounting.utils.logging._
import com.colofabrix.scala.accounting.utils.validation._

/**
 * Client interface
 */
object Client extends PureLogging[IO] {
  type ClientOutput[A] = IO[Either[ErrorInfo, A]]

  /**
   * Returns the list of supported input types
   */
  def listSupportedInputs: ClientOutput[Set[InputType]] = {
    for {
      _      <- ECManager.shift[IO](DefaultEC.compute)
      _      <- pureLogger.info("Requested listSupportedInputs")
      result <- IO(serviceConfig.inputTypes.asRight)
    } yield result
  }

  // The caller should set the thread pool, the called should introduce async boundaries

  /**
   * Converts one single input record into one output transaction
   */
  @SuppressWarnings(Array("org.wartremover.warts.TraversableOps"))
  def convertRecord(inputType: InputType, record: String): ClientOutput[AValidated[Transaction]] = {
    logger.info(s"Requested convertRecord with input type ${inputType.entryName}")

    new CsvReader[IO, String](record)
      .read
      .through(pipelineForType(inputType))
      .compile
      .toList
      .map(_.head.asRight)
  }

  /**
   * Converts a list of inputs records into output transactions
   */
  def convertRecords(inputType: InputType, records: String): ClientOutput[List[AValidated[Transaction]]] = {
    logger.info(s"Requested convertRecords with input type ${inputType.entryName}")

    new CsvReader[IO, String](records)
      .read
      .through(pipelineForType(inputType))
      .compile
      .toList
      .map(_.asRight)
  }
}
