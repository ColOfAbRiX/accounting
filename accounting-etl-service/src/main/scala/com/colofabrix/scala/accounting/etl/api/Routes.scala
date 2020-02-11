package com.colofabrix.scala.accounting.etl.api

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.etl.api._
import com.colofabrix.scala.accounting.etl.client._
import org.http4s.HttpRoutes
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.redoc.http4s.RedocHttp4s
import sttp.tapir.server.http4s._

object Routes {

  def listSupportedInputsRoute(cs: ContextShift[IO]): HttpRoutes[IO] = {
    implicit val ics: ContextShift[IO] = cs
    Endpoints.listSupportedInputs.toRoutes(_ => Client.listSupportedInputs)
  }

  def convertRecordRoute(cs: ContextShift[IO]): HttpRoutes[IO] = {
    implicit val ics: ContextShift[IO] = cs
    Endpoints.convertRecord.toRoutes((Client.convertRecord _).tupled)
  }

  def convertRecordsRoute(cs: ContextShift[IO]): HttpRoutes[IO] = {
    implicit val ics: ContextShift[IO] = cs
    Endpoints.convertRecords.toRoutes((Client.convertRecords _).tupled)
  }

  def redocDocsRoute(cs: ContextShift[IO]): HttpRoutes[IO] = {
    implicit val ics: ContextShift[IO] = cs
    new RedocHttp4s("Accounting ETL API", Endpoints.openApiDocsEndpoint.toYaml).routes
  }

  def allRoutes(cs: ContextShift[IO]): HttpRoutes[IO] = {
    val allRoutes = List[HttpRoutes[IO]](
      listSupportedInputsRoute(cs),
      convertRecordRoute(cs),
      convertRecordsRoute(cs),
    )
    allRoutes.foldLeft(HttpRoutes.empty[IO])(_ <+> _)
  }

}
