package com.colofabrix.scala.accounting.transactions.api

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.transactions.BuildInfo
import com.colofabrix.scala.accounting.transactions.client._
import com.colofabrix.scala.accounting.transactions.model.Api._
import com.colofabrix.scala.accounting.utils.logging._
import io.circe.generic.auto._
import org.http4s._
import org.http4s.server.Router
import org.http4s.syntax.kleisli._
import sttp.model._
import sttp.tapir._
import sttp.tapir.docs.openapi._
import sttp.tapir.json.circe._
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.redoc.http4s.RedocHttp4s
import sttp.tapir.server._
import sttp.tapir.server.http4s._

/**
 * Endpoint definition for Transactions
 */
abstract class TransactionsEndpoints[F[_]: Sync] {
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
final class TransactionsEndpointsImpl(client: TransactionsClient[IO])
    extends TransactionsEndpoints[IO]
    with PureLogging {
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
      msg <- IO(GenericExceptionError(t.toString()).asLeft[A])
      _   <- pureLogger.throwable[IO](t, s"Generic API error")
    } yield msg

  private[this] val test: ShortEndpoint[Unit, String] =
    apiBaseEndpoint
      .get
      .in("test")
      .out(jsonBody[String])
      .name("test")
      .serverLogic { _ =>
        client
          .test
          .map(_.asRight[ErrorInfo])
          .handleErrorWith(handleEndpointErrors)
      }

  /** The list of all endpoints */
  val allEndpoints: List[ShortEndpoint[_, _]] = List(test)

  /** Endpoint for the documentation */
  val docsEndpoint: OpenAPI = allEndpoints.toOpenAPI(BuildInfo.description, apiVersion)
}
