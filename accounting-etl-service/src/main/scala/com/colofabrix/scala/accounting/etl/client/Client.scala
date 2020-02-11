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

object Client {

  /**
   * Returns the list of supported input types
   */
  def listSupportedInputs: ClientOutput[Set[InputType]] = IO.delay {
    etlConfig.inputTypes.asRight
  }

  /**
   * Converts one single input record into one output transaction
   */
  @SuppressWarnings(Array("org.wartremover.warts.All"))
  def convertRecord(inputType: InputType, record: String): ClientOutput[AValidated[Transaction]] = {
    new CsvReader(record)
      .read
      .through(pipelineForType(inputType))
      .compile
      .toList
      .map(_.head.asRight)
  }

  /**
   * Converts a list of inputs records into output transactions
   */
  @SuppressWarnings(Array("org.wartremover.warts.All"))
  def convertRecords(inputType: InputType, body: String): ClientOutput[String] = {
    ???
  }

}
