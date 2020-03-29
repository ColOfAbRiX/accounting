package com.colofabrix.scala.accounting.etl.client

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.etl.config._
import com.colofabrix.scala.accounting.etl.model.Api._
import com.colofabrix.scala.accounting.etl.model.Config._
import com.colofabrix.scala.accounting.etl.pipeline.ApiPipelineInstances._
import com.colofabrix.scala.accounting.etl.readers.CsvReader
import com.colofabrix.scala.accounting.model.SingleTransaction
import com.colofabrix.scala.accounting.utils.concurrency._
import com.colofabrix.scala.accounting.utils.logging._
import com.colofabrix.scala.accounting.utils.validation._

/**
 * Client interface
 */
object Client extends PureLogging {
  protected[this] val logger = org.log4s.getLogger

  /**
   * Returns the list of supported input types
   */
  def listSupportedInputs[F[_]: Sync: ContextShift]: F[Either[ErrorInfo, Set[InputType]]] = {
    val computation = for {
      _      <- pureLogger.info[F]("Requested listSupportedInputs")
      result <- Sync[F].delay(serviceConfig.inputTypes.asRight[ErrorInfo])
    } yield result

    ContextShift[F].evalOn(DefaultEC.compute)(computation)
  }

  /**
   * Converts one single input record into one output transaction
   */
  @SuppressWarnings(Array("org.wartremover.warts.TraversableOps"))
  def convertRecord[F[_]: Sync: ContextShift](
      inputType: InputType,
      record: String,
  ): F[Either[ErrorInfo, AValidated[SingleTransaction]]] = {
    logger.info(s"Requested convertRecord with input type ${inputType.entryName}")

    val computation = new CsvReader[F, String](record)
      .read
      .through(pipelineForType(inputType))
      .compile
      .toList
      .map(_.head.asRight[ErrorInfo])

    ContextShift[F].evalOn(DefaultEC.compute)(computation)
  }

  /**
   * Converts a list of input records into output transactions
   */
  def convertRecords[F[_]: Sync: ContextShift](
      inputType: InputType,
      records: String,
  ): F[Either[ErrorInfo, List[AValidated[SingleTransaction]]]] = {
    logger.info(s"Requested convertRecords with input type ${inputType.entryName}")

    val computation = new CsvReader[F, String](records)
      .read
      .through(pipelineForType(inputType))
      .compile
      .toList
      .map(_.asRight[ErrorInfo])

    ContextShift[F].evalOn(DefaultEC.compute)(computation)
  }
}
