package com.colofabrix.scala.accounting.etl.client

import cats.effect._
import com.colofabrix.scala.accounting.etl.config._
import com.colofabrix.scala.accounting.etl.model.Config._
import com.colofabrix.scala.accounting.etl.pipeline.ApiPipelineInstances._
import com.colofabrix.scala.accounting.etl.readers.CsvReader
import com.colofabrix.scala.accounting.model.SingleTransaction
import com.colofabrix.scala.accounting.utils.logging._
import com.colofabrix.scala.accounting.utils.validation._

/**
 * ETL Client interface
 */
trait EtlClient[F[_]] {
  def listSupportedInputs: F[Set[InputType]]
  def convertRecord(inputType: InputType, record: String): F[AValidated[SingleTransaction]]
  def convertRecords(inputType: InputType, records: String): F[List[AValidated[SingleTransaction]]]
}

/**
 * ETL Client standard implementation
 */
final class EtlClientImpl(cs: ContextShift[IO]) extends EtlClient[IO] with PureLogging {
  implicit private[this] val ics: ContextShift[IO] = cs

  protected[this] val logger = org.log4s.getLogger

  /**
   * Returns the list of supported input types
   */
  def listSupportedInputs: IO[Set[InputType]] =
    for {
      _      <- pureLogger.info[IO]("Requested listSupportedInputs")
      result <- IO(serviceConfig.inputTypes)
    } yield result

  /**
   * Converts one single input record into one output transaction
   */
  @SuppressWarnings(Array("org.wartremover.warts.TraversableOps"))
  def convertRecord(inputType: InputType, record: String): IO[AValidated[SingleTransaction]] =
    for {
      _ <- pureLogger.info[IO](s"Requested convertRecord with input type ${inputType.entryName}")
      r <- CsvReader[IO, String](record)
            .read
            .through(pipelineForType(inputType))
            .compile
            .toList
            .map(_.head)
    } yield r

  /**
   * Converts a list of input records into output transactions
   */
  def convertRecords(inputType: InputType, records: String): IO[List[AValidated[SingleTransaction]]] =
    for {
      _ <- pureLogger.info[IO](s"Requested convertRecords with input type ${inputType.entryName}")
      r <- CsvReader[IO, String](records)
            .read
            .through(pipelineForType(inputType))
            .compile
            .toList
    } yield r
}
