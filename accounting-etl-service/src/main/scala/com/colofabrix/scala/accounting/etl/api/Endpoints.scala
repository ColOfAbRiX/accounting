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

  val baseEndpoint: Endpoint[Unit, ErrorOutput, Unit, Nothing] = endpoint
    .in("api" / "1.0.0")
    .errorOut(statusCode and jsonBody[ErrorInfo])

  val listSupportedInputs: Endpoint[Unit, ErrorOutput, String, Nothing] =
    baseEndpoint
      .get
      .in("supported-inputs")
      .out(stringBody)

  val convertStream: Endpoint[(InputType, String), ErrorOutput, String, Nothing] =
    baseEndpoint
      .get
      .in("convert-stream")
      .in(recordTypeQuery)
      .in(stringBody)
      .out(stringBody)

  val convertRecord: Endpoint[(InputType, String), ErrorOutput, String, Nothing] =
    baseEndpoint
      .get
      .in("convert-record")
      .in(recordTypeQuery)
      .in(stringBody)
      .out(stringBody)

  val convertRecords: Endpoint[(InputType, String), ErrorOutput, String, Nothing] =
    baseEndpoint
      .get
      .in("convert-records")
      .in(recordTypeQuery)
      .in(stringBody)
      .out(stringBody)

  @SuppressWarnings(Array("org.wartremover.warts.PublicInference"))
  val allEndpoints = List(
    listSupportedInputs,
    convertStream,
    convertRecord,
    convertRecords,
  )

  val openApiDocsEndpoint: OpenAPI = allEndpoints
    .toOpenAPI("Accounting ETL Service", "1.0.0")
}
