package com.colofabrix.scala.accounting.transactions.api

import com.colofabrix.scala.accounting.transactions.BuildInfo
import com.colofabrix.scala.accounting.transactions.model.Api._
import io.circe.generic.auto._
import sttp.tapir._
import sttp.tapir.docs.openapi._
import sttp.tapir.json.circe._
import sttp.tapir.openapi.OpenAPI

/**
 * Endpoints describe what's exposed
 */
object Endpoints {

  /** The version of the API */
  val apiVersion: String = "v1.0"

  /**
   * Base endpoint of all APIs
   */
  val apiBaseEndpoint: Endpoint[Unit, ErrorInfo, Unit, Nothing] = {
    endpoint
      .in("api" / apiVersion)
      .errorOut(jsonBody[ErrorInfo])
  }

  val getTransactions: Endpoint[Unit, ErrorInfo, String, Nothing] = {
    apiBaseEndpoint
      .get
      .out(plainBody[String])
      .name("transactions")
      .description(
        """Retrieves the list of all transactions in the database in a redacted form""".stripMargin,
      )
  }

  val getTransaction: Endpoint[Unit, ErrorInfo, String, Nothing] = {
    apiBaseEndpoint
      .get
      .out(plainBody[String])
      .name("transaction")
      .description(
        """Returns informations about a single transaction""".stripMargin,
      )
  }

  val postTransaction: Endpoint[Unit, ErrorInfo, String, Nothing] = {
    apiBaseEndpoint
      .post
      .out(plainBody[String])
      .name("transaction")
      .description(
        """Adds a new transaction""".stripMargin,
      )
  }

  val patchTransaction: Endpoint[Unit, ErrorInfo, String, Nothing] = {
    apiBaseEndpoint
      .patch
      .out(plainBody[String])
      .name("transaction")
      .description(
        """Updates an existing transaction""".stripMargin,
      )
  }

  val deleteTransaction: Endpoint[Unit, ErrorInfo, String, Nothing] = {
    apiBaseEndpoint
      .patch
      .out(plainBody[String])
      .name("transaction")
      .description(
        """Deletes an existing transaction""".stripMargin,
      )
  }

  // POST: transaction-batch
  // PATCH: transaction-batch
  // DELETE: transaction-batch

  /**
   * The API documentation endpoint
   */
  val openApiDocsEndpoint: OpenAPI = {
    List[Endpoint[_, _, _, _]](
      getTransactions,
      getTransaction,
      postTransaction,
      patchTransaction,
      deleteTransaction,
    ).toOpenAPI(
      BuildInfo.description,
      apiVersion,
    )
  }

}
