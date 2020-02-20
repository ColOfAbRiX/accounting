package com.colofabrix.scala.accounting.etl.api

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.etl.api._
import com.colofabrix.scala.accounting.etl.client._
import com.colofabrix.scala.accounting.etl.BuildInfo
import org.http4s.HttpRoutes
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.redoc.http4s.RedocHttp4s
import sttp.tapir.server.http4s._
import com.colofabrix.scala.accounting.utils.ThreadPools

object Routes {

  implicit private[this] val ics: ContextShift[IO] = ThreadPools.globalCs

  val listSupportedInputsRoute: HttpRoutes[IO] = Endpoints.listSupportedInputs.toRoutes(_ => Client.listSupportedInputs)
  val convertRecordRoute: HttpRoutes[IO]       = Endpoints.convertRecord.toRoutes((Client.convertRecord _).tupled)
  val convertRecordsRoute: HttpRoutes[IO]      = Endpoints.convertRecords.toRoutes((Client.convertRecords _).tupled)
  val redocDocsRoute: HttpRoutes[IO] = {
    new RedocHttp4s(BuildInfo.description, Endpoints.openApiDocsEndpoint.toYaml).routes
  }

  def allRoutes: HttpRoutes[IO] = {
    val allRoutes = List[HttpRoutes[IO]](
      listSupportedInputsRoute,
      convertRecordRoute,
      convertRecordsRoute,
    )
    allRoutes.foldLeft(HttpRoutes.empty[IO])(_ <+> _)
  }

}
