package com.colofabrix.scala.accounting.etl.api

import cats.data._
import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.etl.BuildInfo
import com.colofabrix.scala.accounting.etl.api.CirceCodecs._
import com.colofabrix.scala.accounting.etl.api.TapirInputs._
import com.colofabrix.scala.accounting.etl.client.EtlClient
import com.colofabrix.scala.accounting.etl.model.Api._
import com.colofabrix.scala.accounting.etl.model.Config._
import com.colofabrix.scala.accounting.etl.refined.tapir._
import com.colofabrix.scala.accounting.model.SingleTransaction
import com.colofabrix.scala.accounting.utils.logging._
import com.colofabrix.scala.accounting.utils.validation._
import eu.timepit.refined.auto._
import io.circe.generic.auto._
import io.circe.refined._
import org.http4s._
import org.http4s.server.Router
import org.http4s.syntax.kleisli._
import sttp.model._
import sttp.tapir._
import sttp.tapir.codec.cats._
import sttp.tapir.docs.openapi._
import sttp.tapir.json.circe._
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.redoc.http4s.RedocHttp4s
import sttp.tapir.server._
import sttp.tapir.server.http4s._

/**
 * Endpoint definition for ETL
 */
abstract class EtlEndpoints[F[_]: Sync] {
  protected type ShortEndpoint[I, O] = ServerEndpoint[I, ErrorInfo, O, Nothing, F]

  /** The list of all endpoints */
  protected def allEndpoints: List[ShortEndpoint[_, _]]

  /** Endpoint for the documentation */
  protected def docsEndpoint: OpenAPI

  /** The definition of the http4s Http Application */
  def app(cs: ContextShift[F]): HttpApp[F] = {
    implicit val ics: ContextShift[F] = cs

    val allRoutes = allEndpoints.toRoutes
    val docsRoute = new RedocHttp4s(BuildInfo.description, docsEndpoint.toYaml).routes

    Router(
      "/"     -> allRoutes,
      "/docs" -> docsRoute,
    ).orNotFound
  }
}

/**
 * Endpoints describe what's exposed
 */
final class EtlEndpointsImpl(client: EtlClient[IO]) extends EtlEndpoints[IO] with PureLogging {
  protected[this] val logger = org.log4s.getLogger

  /** Mapping between the internal representation of API errors to HTTP errors */
  private[this] val errorsMapping: EndpointOutput.OneOf[ErrorInfo, ErrorInfo] = oneOf(
    statusMapping(
      StatusCode.InternalServerError,
      jsonBody[GenericExceptionError].description("Generic server exception"),
    ),
    statusDefaultMapping(jsonBody[UnknownError].description("Unknown error")),
  )

  /** The version of the API */
  private[this] val apiVersion: String = "v1.0"

  private[this] val apiBaseEndpoint: Endpoint[Unit, ErrorInfo, Unit, Nothing] = {
    endpoint
      .in("api" / apiVersion)
      .errorOut(errorsMapping)
  }

  /** How endpoints handle Exceptions */
  private[this] def handleEndpointErrors[A](t: Throwable): IO[Either[ErrorInfo, A]] =
    for {
      msg <- IO(GenericExceptionError(t.toString).asLeft[A])
      _   <- pureLogger.throwable[IO](t, s"Generic API error")
    } yield msg

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
        client
          .listSupportedInputs
          .map(_.asRight[ErrorInfo])
          .handleErrorWith(x => handleEndpointErrors(x))
      }

  /**
   * Converts one single input record into one output transaction
   */
  private[this] val convertRecord: ShortEndpoint[(InputType, String), AValidated[SingleTransaction]] = {
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
          client
            .convertRecord(inputType, body)
            .map(_.asRight[ErrorInfo])
            .handleErrorWith(x => handleEndpointErrors(x))
      }
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
          client
            .convertRecords(inputType, body)
            .map(_.asRight[ErrorInfo])
            .handleErrorWith(x => handleEndpointErrors(x))
      }

  /** The list of all endpoints */
  val allEndpoints: List[ShortEndpoint[_, _]] = List(
    listSupportedInputs,
    convertRecord,
    convertRecords,
  )

  /** Endpoint for the documentation */
  val docsEndpoint: OpenAPI = allEndpoints.toOpenAPI(BuildInfo.description, apiVersion)
}
