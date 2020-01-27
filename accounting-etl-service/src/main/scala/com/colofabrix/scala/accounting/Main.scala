package com.colofabrix.scala.accounting

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.etl.api._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.Router
import org.http4s.syntax.kleisli._
import org.http4s._

object MultipleEndpointsDocumentationHttp4sServer extends IOApp {

  def httpApp: HttpApp[IO] =
    Router(
      "/"     -> Routes.allRoutes(contextShift),
      "/docs" -> Routes.redocDocsRoute(contextShift),
    ).orNotFound

  override def run(args: List[String]): IO[ExitCode] = {
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(httpApp)
      .resource
      .use(_ => IO(scala.io.StdIn.readLine()))
      .as(ExitCode.Success)
  }

}
