package com.colofabrix.scala.accounting.etl.api

import cats.data._
import com.colofabrix.scala.accounting.etl.api.CirceCodecs._
import com.colofabrix.scala.accounting.etl.api.Inputs._
import com.colofabrix.scala.accounting.etl.model.Api._
import com.colofabrix.scala.accounting.etl.model.Config._
import com.colofabrix.scala.accounting.etl.BuildInfo
import com.colofabrix.scala.accounting.model.SingleTransaction
import com.colofabrix.scala.accounting.utils.validation._
import com.colofabrix.scala.accounting.utils.validation.{ ValidationError => VError }
import io.circe.generic.auto._
import sttp.tapir._
import sttp.tapir.codec.cats._
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
  val apiBaseEndpoint: EtlEndpoint[Unit, Unit] = {
    endpoint
      .in("api" / apiVersion)
      .errorOut(jsonBody[ErrorInfo])
  }

  /**
   * Returns the list of supported input types
   */
  val listSupportedInputs: EtlEndpoint[Unit, Set[InputType]] = {
    apiBaseEndpoint
      .get
      .in("supported-inputs")
      .out(jsonBody[Set[InputType]])
      .name("supported-inputs")
      .description(
        """Lists the type of inputs supported by the service.
          |The list of enabled inputs can be set in the application configuration file.""".stripMargin,
      )
  }

  /**
   * Converts one single input record into one output transaction
   */
  val convertRecord: EtlEndpoint[(InputType, String), AValidated[SingleTransaction]] = {
    apiBaseEndpoint
      .get
      .in("convert-record")
      .in(inputTypeQuery)
      .in(stringBody)
      .out(jsonBody[Validated[NonEmptyChain[VError], SingleTransaction]])
      .name("convert-record")
      .description(
        """Validates and converts one single input record, interpreted as a CSV row
          |into one output transaction.""".stripMargin,
      )
  }

  /**
   * Converts a list of inputs records into output transactions
   */
  val convertRecords: EtlEndpoint[(InputType, String), List[AValidated[SingleTransaction]]] = {
    apiBaseEndpoint
      .get
      .in("convert-records")
      .in(inputTypeQuery)
      .in(stringBody)
      .out(jsonBody[List[Validated[NonEmptyChain[VError], SingleTransaction]]])
      .name("convert-records")
      .description(
        """Validates and converts a list of input records, interpreted as CSV data
          |into a list of output transaction.""".stripMargin,
      )
  }

  /**
   * The API documentation endpoint
   */
  val openApiDocsEndpoint: OpenAPI = {
    List(
      listSupportedInputs,
      convertRecord,
      convertRecords,
    ).toOpenAPI(
      BuildInfo.description,
      apiVersion,
    )
  }

}
