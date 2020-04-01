package com.colofabrix.scala.accounting.etl.client

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.etl.config._
import com.colofabrix.scala.accounting.etl.model.Api._
import com.colofabrix.scala.accounting.etl.model.Config._
import com.colofabrix.scala.accounting.etl.pipeline.ApiPipelineInstances._
import com.colofabrix.scala.accounting.etl.readers.CsvReader
import com.colofabrix.scala.accounting.model.SingleTransaction
import com.colofabrix.scala.accounting.utils.logging._
import com.colofabrix.scala.accounting.utils.validation._

/**
 * Client interface
 */
final class Client[F[_]: Sync: ContextShift] extends PureLogging {
  protected[this] val logger = org.log4s.getLogger

  /**
   * Returns the list of supported input types
   */
  def listSupportedInputs: F[Either[ErrorInfo, Set[InputType]]] =
    for {
      _      <- pureLogger.info[F]("Requested listSupportedInputs")
      result <- Sync[F].delay(serviceConfig.inputTypes.asRight[ErrorInfo])
    } yield result

  /**
   * Converts one single input record into one output transaction
   */
  @SuppressWarnings(Array("org.wartremover.warts.TraversableOps"))
  def convertRecord(inputType: InputType, record: String): F[Either[ErrorInfo, AValidated[SingleTransaction]]] =
    for {
      _ <- pureLogger.info(s"Requested convertRecord with input type ${inputType.entryName}")
      r <- CsvReader[F, String](record)
            .read
            .through(pipelineForType(inputType))
            .compile
            .toList
            .map(_.head.asRight[ErrorInfo])
    } yield r

  /**
   * Converts a list of input records into output transactions
   */
  def convertRecords(inputType: InputType, records: String): F[Either[ErrorInfo, List[AValidated[SingleTransaction]]]] =
    for {
      _ <- pureLogger.info(s"Requested convertRecords with input type ${inputType.entryName}")
      r <- CsvReader[F, String](records)
            .read
            .through(pipelineForType(inputType))
            .compile
            .toList
            .map(_.asRight[ErrorInfo])
    } yield r
}

object Client {
  def apply[F[_]: Sync: ContextShift]: Client[F] = new Client[F]
}
