package com.colofabrix.scala.accounting.transactionsdb.api

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.transactionsdb.BuildInfo
import com.colofabrix.scala.accounting.utils.ThreadPools
import org.http4s.HttpRoutes
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.redoc.http4s.RedocHttp4s

/**
 * Routes receive requests and compute responses
 */
object Routes {

  implicit private[this] val ics: ContextShift[IO] = ThreadPools.globalCs

  val redocDocsRoute: HttpRoutes[IO] = {
    new RedocHttp4s(BuildInfo.description, Endpoints.openApiDocsEndpoint.toYaml).routes
  }

  def allRoutes: HttpRoutes[IO] = {
    val allRoutes = List.empty[HttpRoutes[IO]]
    allRoutes.foldLeft(HttpRoutes.empty[IO])(_ <+> _)
  }

}
