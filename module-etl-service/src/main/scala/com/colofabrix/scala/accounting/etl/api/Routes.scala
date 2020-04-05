package com.colofabrix.scala.accounting.etl.api

import cats._
import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.etl.api._
import com.colofabrix.scala.accounting.etl.BuildInfo
import com.colofabrix.scala.accounting.etl.client._
import com.colofabrix.scala.accounting.etl.model.Api._
import org.http4s.HttpRoutes
import shapeless._
import shapeless.ops.function._
import shapeless.syntax.std.function._
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.redoc.http4s.RedocHttp4s
import sttp.tapir.server.http4s._

/**
 * Http4s routes
 */
object Routes {
  implicit private[this] val cs: ContextShift[IO] = implicitly[ContextShift[IO]]

  private[this] def applyUnitClient[F[_]: Functor, A](f: => F[A]): Unit => F[Either[ErrorInfo, A]] = { _ =>
    f.map(_.asRight[ErrorInfo])
  }

  private[this] def applyClient[P, G, L <: HList, R](f: G)(
      implicit
      gen: Generic.Aux[P, L],
      ftp: FnToProduct.Aux[G, L => IO[R]],
  ): P => IO[Either[ErrorInfo, R]] = { p =>
    f.toProduct(gen.to(p)).map(_.asRight[ErrorInfo])
  }

  /** Route to "listSupportedInputs" */
  private[this] val listSupportedInputsRoute: HttpRoutes[IO] = Endpoints
    .listSupportedInputs
    .toRoutes(applyUnitClient(Client.listSupportedInputs))

  /** Route to convertRecordRoute */
  private[this] val convertRecordRoute: HttpRoutes[IO] = Endpoints
    .convertRecord
    .toRoutes(applyClient(Client.convertRecord _))

  /** Route to convertRecordsRoute */
  private[this] val convertRecordsRoute: HttpRoutes[IO] = Endpoints
    .convertRecords
    .toRoutes(applyClient(Client.convertRecords _))

  /** Route to documentation */
  val docsRoute: HttpRoutes[IO] =
    new RedocHttp4s(BuildInfo.description, Endpoints.openApiDocsEndpoint.toYaml).routes

  /** List of all available routes except documentation */
  val allRoutes: List[HttpRoutes[IO]] = List(
    listSupportedInputsRoute,
    convertRecordRoute,
    convertRecordsRoute,
  )
}
