package com.colofabrix.scala.accounting.trnxdb.api

import cats.effect._
import cats.implicits._
import org.http4s.HttpRoutes
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.redoc.http4s.RedocHttp4s
import sttp.tapir.server.http4s._
import com.colofabrix.scala.accounting.utils.ThreadPools

/**
 * Routes receive requests and compute responses
 */
object Routes {

  implicit private[this] val ics: ContextShift[IO] = ThreadPools.globalCs

  val redocDocsRoute: HttpRoutes[IO] = {
    new RedocHttp4s("Accounting Transactions DB Service", Endpoints.openApiDocsEndpoint.toYaml).routes
  }

  def allRoutes: HttpRoutes[IO] = {
    val allRoutes = List.empty[HttpRoutes[IO]]
    allRoutes.foldLeft(HttpRoutes.empty[IO])(_ <+> _)
  }

}
