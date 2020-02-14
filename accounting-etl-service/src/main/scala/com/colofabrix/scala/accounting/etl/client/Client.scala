package com.colofabrix.scala.accounting.etl.client

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.etl.pipeline.ApiPipelineInstances._
import com.colofabrix.scala.accounting.etl.config._
import com.colofabrix.scala.accounting.etl.conversion.CsvReader
import com.colofabrix.scala.accounting.etl.model.Api._
import com.colofabrix.scala.accounting.etl.model.Config._
import com.colofabrix.scala.accounting.model.Transaction
import com.colofabrix.scala.accounting.utils.validation._
import org.log4s._

object Client {

  private[this] val logger = getLogger

  /**
   * Returns the list of supported input types
   */
  def listSupportedInputs: ClientOutput[Set[InputType]] = {
    logger.info("Requested listSupportedInputs")

    IO.delay {
      etlConfig.inputTypes.asRight
    }
  }

  /**
   * Converts one single input record into one output transaction
   */
  @SuppressWarnings(Array("org.wartremover.warts.TraversableOps"))
  def convertRecord(inputType: InputType, record: String): ClientOutput[AValidated[Transaction]] = {
    logger.info("Requested convertRecord")
    logger.debug(s"inputType=${inputType.description}, record=${record}")

    new CsvReader(record)
      .read
      .map { x =>
        println(s"csvPipeline - Thread name: ${Thread.currentThread.getName}")
        x
      }
      .through(pipelineForType(inputType))
      .map { x =>
        println(s"csvPipeline2 - Thread name: ${Thread.currentThread.getName}")
        x
      }
      .compile
      .toList
      .map(_.head.asRight)
  }

  /**
   * Converts a list of inputs records into output transactions
   */
  def convertRecords(inputType: InputType, records: String): ClientOutput[List[AValidated[Transaction]]] = {
    logger.info("Requested convertRecords")
    logger.debug(s"inputType=${inputType.description}, records=${records}")

    new CsvReader(records)
      .read
      .through(pipelineForType(inputType))
      .compile
      .toList
      .map(_.asRight)
  }

}
