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
 * Common interface to gather all routes
 */
trait AllRoutes[F[_]] {
  /** List of all routes */
  def allRoutes: List[HttpRoutes[F]]

  /** Route to the documentation */
  def docsRoute: HttpRoutes[F]
}

/**
 * Http4s routes
 */
final class Routes[F[_]: Sync: ContextShift] extends AllRoutes[F] {
  val listSupportedInputsRoute: HttpRoutes[F] = Endpoints
    .listSupportedInputs
    .toRoutes(Routes.applyUnitClient(Client[F].listSupportedInputs))

  val convertRecordRoute: HttpRoutes[F] = Endpoints
    .convertRecord
    .toRoutes(Routes.applyClient(Client[F].convertRecord _))

  val convertRecordsRoute: HttpRoutes[F] = Endpoints
    .convertRecords
    .toRoutes(Routes.applyClient(Client[F].convertRecords _))

  val docsRoute: HttpRoutes[F] =
    new RedocHttp4s(BuildInfo.description, Endpoints.openApiDocsEndpoint.toYaml).routes

  val allRoutes: List[HttpRoutes[F]] = List(
    listSupportedInputsRoute,
    convertRecordRoute,
    convertRecordsRoute,
  )
}

object Routes {
  def apply[F[_]: Sync: ContextShift]: AllRoutes[F] = new Routes[F]

  private[Routes] def applyUnitClient[F[_]: Functor, A](f: => F[A]): Unit => F[Either[ErrorInfo, A]] = { _ =>
    f.map(_.asRight[ErrorInfo])
  }

  private[Routes] def applyClient[F[_]: Sync, P, G, L <: HList, R](f: G)(
      implicit
      gen: Generic.Aux[P, L],
      ftp: FnToProduct.Aux[G, L => F[R]],
  ): P => F[Either[ErrorInfo, R]] = { p =>
    Sync[F].map(f.toProduct(gen.to(p)))(_.asRight[ErrorInfo])
  }
}
