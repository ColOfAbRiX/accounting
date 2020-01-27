package com.colofabrix.scala.accounting.etl.api

import com.colofabrix.scala.accounting.etl.api.Inputs._
import com.colofabrix.scala.accounting.model._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.OpenAPI

/**
 * Endpoints describe what's exposed
 */
object Endpoints {

  type ErrorInfo   = String
  type ErrorOutput = (StatusCode, ErrorInfo)

  val apiVersion: String = "v1.0"

  val apiBaseEndpoint: Endpoint[Unit, ErrorOutput, Unit, Nothing] = {
    endpoint
      .in("api" / apiVersion)
      .errorOut(statusCode and jsonBody[ErrorInfo])
  }

  /**
   * Returns the list of supported input types
   */
  val listSupportedInputs: Endpoint[Unit, ErrorOutput, String, Nothing] = {
    apiBaseEndpoint
      .get
      .in("supportedInputs")
      .out(stringBody)
  }

  /**
   * Converts a stream of records
   */
  val convertStream: Endpoint[(InputType, String), ErrorOutput, String, Nothing] = {
    apiBaseEndpoint
      .get
      .in("convertStream")
      .in(recordTypeQuery)
      .in(stringBody)
      .out(stringBody)
  }

  /**
   * Converts one single input record into one output transaction
   */
  val convertRecord: Endpoint[(InputType, String), ErrorOutput, String, Nothing] = {
    apiBaseEndpoint
      .get
      .in("convertRecord")
      .in(recordTypeQuery)
      .in(stringBody)
      .out(stringBody)
  }

  /**
   * Converts a list of inputs records into output transactions
   */
  val convertRecords: Endpoint[(InputType, String), ErrorOutput, String, Nothing] = {
    apiBaseEndpoint
      .get
      .in("convertRecords")
      .in(recordTypeQuery)
      .in(stringBody)
      .out(stringBody)
  }

  /**
   * The API documentation endpoint
   */
  val openApiDocsEndpoint: OpenAPI = {
    List(
      listSupportedInputs,
      convertStream,
      convertRecord,
      convertRecords,
    ).toOpenAPI("Accounting ETL Service", apiVersion)
  }

}
