package com.colofabrix.scala.accounting.etl.api

import cats.effect._
import com.colofabrix.scala.accounting.etl.api._
import com.colofabrix.scala.accounting.etl.BuildInfo
import org.http4s.HttpRoutes
import org.http4s.HttpRoutes
import shapeless._
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.redoc.http4s.RedocHttp4s
import sttp.tapir.server.http4s._

/**
 * Http4s routes
 */
object Routes {
  implicit private[this] val cs: ContextShift[IO] = implicitly[ContextShift[IO]]

  /** List of all available routes except documentation */
  val allRoutes: HttpRoutes[IO] = Endpoints.allEndpoints.toRoutes

  /** Route to documentation */
  val docsRoute: HttpRoutes[IO] =
    new RedocHttp4s(BuildInfo.description, Endpoints.docsEndpoint.toYaml).routes
}
