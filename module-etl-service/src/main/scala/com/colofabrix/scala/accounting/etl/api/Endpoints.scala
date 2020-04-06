package com.colofabrix.scala.accounting.etl.api

import cats.data._
import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.etl.api.CirceCodecs._
import com.colofabrix.scala.accounting.etl.api.Inputs._
import com.colofabrix.scala.accounting.etl.BuildInfo
import com.colofabrix.scala.accounting.etl.client.Client
import com.colofabrix.scala.accounting.etl.model.Api._
import com.colofabrix.scala.accounting.etl.model.Config._
import com.colofabrix.scala.accounting.model.SingleTransaction
import com.colofabrix.scala.accounting.utils.logging._
import com.colofabrix.scala.accounting.utils.validation._
import io.circe.generic.auto._
import sttp.model._
import sttp.tapir._
import sttp.tapir.codec.cats._
import sttp.tapir.docs.openapi._
import sttp.tapir.json.circe._
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.server._

/**
 * Endpoints describe what's exposed
 */
object Endpoints extends PureLogging {
  protected[this] val logger = org.log4s.getLogger

  private[this] type ShortEndpoint[I, O] = ServerEndpoint[I, ErrorInfo, O, Nothing, IO]

  /**
   * The version of the API
   */
  private[this] val apiVersion: String = "v1.0"

  /**
   * Mapping between the internal representation of API errors to HTTP errors
   */
  private[this] val errorsMapping: EndpointOutput.OneOf[ErrorInfo] = oneOf(
    statusMapping(
      StatusCode.InternalServerError,
      jsonBody[GenericExceptionError].description("Generic server exception"),
    ),
    statusDefaultMapping(jsonBody[UnknownError].description("Unknown error")),
  )

  /**
   * How endpoints handle Exceptions
   */
  private[this] def handleEndpointErrors[A](t: Throwable): IO[Either[ErrorInfo, A]] =
    for {
      msg <- IO(GenericExceptionError(t.toString()).asLeft[A])
      _   <- pureLogger.throwable[IO](t, s"Generic API error")
    } yield msg

  /**
   * Base endpoint of all APIs
   */
  private[this] val apiBaseEndpoint: Endpoint[Unit, ErrorInfo, Unit, Nothing] = {
    endpoint
      .in("api" / apiVersion)
      .errorOut(errorsMapping)
  }

  /**
   * Returns the list of supported input types
   */
  private[this] val listSupportedInputs: ShortEndpoint[Unit, Set[InputType]] =
    apiBaseEndpoint
      .get
      .in("supported-inputs")
      .out(jsonBody[Set[InputType]])
      .name("supported-inputs")
      .description(
        """Lists the type of inputs supported by the service.
          |The list of enabled inputs can be set in the application configuration file.""".stripMargin,
      )
      .serverLogic { _ =>
        Client
          .listSupportedInputs
          .map(_.asRight[ErrorInfo])
          .handleErrorWith(handleEndpointErrors)
      }

  /**
   * Converts one single input record into one output transaction
   */
  private[this] val convertRecord: ShortEndpoint[(InputType, String), AValidated[SingleTransaction]] =
    apiBaseEndpoint
      .get
      .in("convert-record")
      .in(inputTypeQuery)
      .in(stringBody)
      .out(jsonBody[Validated[NonEmptyChain[String], SingleTransaction]])
      .name("convert-record")
      .description(
        """Validates and converts one single input record, interpreted as a CSV row
          |into one output transaction.""".stripMargin,
      )
      .serverLogic {
        case (inputType, body) =>
          Client
            .convertRecord(inputType, body)
            .map(_.asRight[ErrorInfo])
            .handleErrorWith(handleEndpointErrors)
      }

  /**
   * Converts a list of inputs records into output transactions
   */
  private[this] val convertRecords: ShortEndpoint[(InputType, String), List[AValidated[SingleTransaction]]] =
    apiBaseEndpoint
      .get
      .in("convert-records")
      .in(inputTypeQuery)
      .in(stringBody)
      .out(jsonBody[List[Validated[NonEmptyChain[String], SingleTransaction]]])
      .name("convert-records")
      .description(
        """Validates and converts a list of input records, interpreted as CSV data
          |into a list of output transaction.""".stripMargin,
      )
      .serverLogic {
        case (inputType, body) =>
          Client
            .convertRecords(inputType, body)
            .map(_.asRight[ErrorInfo])
            .handleErrorWith(handleEndpointErrors)
      }

  /**
   * List of all endpoints
   */
  @SuppressWarnings(Array("org.wartremover.warts.PublicInference"))
  val allEndpoints = List(listSupportedInputs, convertRecord, convertRecords)

  /**
   * The API documentation endpoint
   */
  val docsEndpoint: OpenAPI = allEndpoints.toOpenAPI(
    BuildInfo.description,
    apiVersion,
  )

}
