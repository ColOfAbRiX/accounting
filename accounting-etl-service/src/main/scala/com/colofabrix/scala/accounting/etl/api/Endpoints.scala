package com.colofabrix.scala.accounting.etl.api

import com.colofabrix.scala.accounting.etl.api.Inputs._
import com.colofabrix.scala.accounting.etl.model.Api._
import com.colofabrix.scala.accounting.etl.model.Config._
import io.circe.generic.auto._
import sttp.tapir._
import sttp.tapir.docs.openapi._
import sttp.tapir.json.circe._
import sttp.tapir.openapi.OpenAPI

/**
 * Endpoints describe what's exposed
 */
object Endpoints {

  val apiVersion: String = "v1.0"

  /**
   * Base endpoint of all APIs
   */
  val apiBaseEndpoint: EtlEndpoint[Unit, Unit] = {
    endpoint
      .in("api" / apiVersion)
      .errorOut(statusCode and jsonBody[ErrorInfo])
  }

  /**
   * Returns the list of supported input types
   */
  val listSupportedInputs: EtlEndpoint[Unit, String] = {
    apiBaseEndpoint
      .get
      .in("supported-inputs")
      .out(stringBody)
  }

  /**
   * Converts one single input record into one output transaction
   */
  val convertRecord: EtlEndpoint[(InputType, String), String] = {
    apiBaseEndpoint
      .get
      .in("convert-record")
      .in(inputTypeQuery)
      .in(stringBody)
      .out(stringBody)
  }

  /**
   * Converts a list of inputs records into output transactions
   */
  val convertRecords: EtlEndpoint[(InputType, String), String] = {
    apiBaseEndpoint
      .get
      .in("convert-records")
      .in(inputTypeQuery)
      .in(stringBody)
      .out(stringBody)
  }

  /**
   * The API documentation endpoint
   */
  val openApiDocsEndpoint: OpenAPI = {
    List(
      listSupportedInputs,
      convertRecord,
      convertRecords,
    ).toOpenAPI("Accounting ETL Service", apiVersion)
  }

}
