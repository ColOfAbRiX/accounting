package com.colofabrix.scala.accounting

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.transactionsdb.config._
import com.colofabrix.scala.accounting.transactionsdb.api.Routes
import org.http4s._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.Router
import org.http4s.syntax.kleisli._
import scala.io.StdIn

object TransactionsDbService extends IOApp {

  private def httpApp: HttpApp[IO] =
    Router(
      "/"     -> Routes.allRoutes,
      "/docs" -> Routes.redocDocsRoute,
    ).orNotFound

  override def run(args: List[String]): IO[ExitCode] = {
    BlazeServerBuilder[IO]
      .bindHttp(serviceConfig.server.port, serviceConfig.server.host)
      .withHttpApp(httpApp)
      .resource
      .use(_ => IO(if (serviceConfig.server.debugMode) StdIn.readLine() else ()))
      .as(ExitCode.Success)
  }

}
