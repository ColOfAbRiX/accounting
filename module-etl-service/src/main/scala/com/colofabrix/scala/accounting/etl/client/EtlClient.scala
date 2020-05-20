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
  /** Returns the list of supported input types */
  def listSupportedInputs: F[Set[InputType]]

  /** Converts one single input record into one output transaction */
  def convertRecord(inputType: InputType, record: String): F[AValidated[SingleTransaction]]

  /** Converts a list of input records into output transactions */
  def convertRecords(inputType: InputType, records: String): F[List[AValidated[SingleTransaction]]]
}

/**
 * ETL Client standard implementation
 */
final class EtlClientImpl(cs: ContextShift[IO]) extends EtlClient[IO] with PureLogging {
  implicit private[this] val ics: ContextShift[IO] = cs

  protected[this] val logger = org.log4s.getLogger

  /** Returns the list of supported input types */
  def listSupportedInputs: IO[Set[InputType]] =
    for {
      _ <- pureLogger.info[IO]("Requested to list the supported inputs (listSupportedInputs)")
      r <- IO(serviceConfig.inputTypes)
    } yield r

  /** Converts one single input record into one output transaction */
  def convertRecord(inputType: InputType, record: String): IO[AValidated[SingleTransaction]] =
    for {
      _ <- pureLogger.info[IO](
            s"Requested to convert a single record with input type '${inputType.entryName}' (convertRecord)",
          )
      r <- CsvReader[IO, String](record)
            .read
            .through(pipelineForType(inputType))
            .compile
            .toList
            .map {
              _.headOption.fold("No transaction present".aInvalid[SingleTransaction])(identity)
            }
    } yield r

  /** Converts a list of input records into output transactions */
  def convertRecords(inputType: InputType, records: String): IO[List[AValidated[SingleTransaction]]] =
    for {
      _ <- pureLogger.info[IO](
            s"Requested to convert multiple records with input type '${inputType.entryName}' (convertRecords)",
          )
      r <- CsvReader[IO, String](records)
            .read
            .through(pipelineForType(inputType))
            .compile
            .toList
    } yield r
}
