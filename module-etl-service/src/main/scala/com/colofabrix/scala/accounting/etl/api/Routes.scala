package com.colofabrix.scala.accounting.etl.api

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.etl.api._
import com.colofabrix.scala.accounting.etl.BuildInfo
import com.colofabrix.scala.accounting.etl.client._
import org.http4s.HttpRoutes
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.redoc.http4s.RedocHttp4s
import sttp.tapir.server.http4s._

/**
 * Http4s routes
 */
final class Routes[F[_]: Sync: ContextShift] {

  val listSupportedInputsRoute: HttpRoutes[F] =
    Endpoints.listSupportedInputs.toRoutes(_ => Client[F].listSupportedInputs)

  val convertRecordRoute: HttpRoutes[F] =
    Endpoints.convertRecord.toRoutes((Client[F].convertRecord _).tupled)

  val convertRecordsRoute: HttpRoutes[F] =
    Endpoints.convertRecords.toRoutes((Client[F].convertRecords _).tupled)

  val redocDocsRoute: HttpRoutes[F] = {
    new RedocHttp4s(BuildInfo.description, Endpoints.openApiDocsEndpoint.toYaml).routes
  }

  def allRoutes: HttpRoutes[F] = {
    val allRoutes = List[HttpRoutes[F]](listSupportedInputsRoute, convertRecordRoute, convertRecordsRoute)
    allRoutes.foldLeft(HttpRoutes.empty[F])(_ <+> _)
  }

}

object Routes {
  def apply[F[_]: Sync: ContextShift]: Routes[F] = new Routes[F]
}
