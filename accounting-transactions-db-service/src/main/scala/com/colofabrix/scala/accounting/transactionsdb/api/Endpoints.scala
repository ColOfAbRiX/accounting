package com.colofabrix.scala.accounting.transactionsdb.api

import com.colofabrix.scala.accounting.BuildInfo
import sttp.tapir._
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.OpenAPI

/**
 * Endpoints describe what's exposed
 */
object Endpoints {

  /** The version of the API */
  val apiVersion: String = "v1.0"

  /**
   * The API documentation endpoint
   */
  val openApiDocsEndpoint: OpenAPI = {
    List[Endpoint[_, _, _, _]]().toOpenAPI(
      BuildInfo.description,
      apiVersion,
    )
  }

}
