package com.colofabrix.scala.accounting

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.etl.api._
import com.colofabrix.scala.accounting.etl.config._
import org.http4s._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.Router
import org.http4s.syntax.kleisli._

object AccountingEtlService extends IOApp {

  private def httpApp: HttpApp[IO] =
    Router(
      "/"     -> Routes.allRoutes,
      "/docs" -> Routes.redocDocsRoute,
    ).orNotFound

  override def run(args: List[String]): IO[ExitCode] = {
    BlazeServerBuilder[IO]
      .bindHttp(etlConfig.server.port, etlConfig.server.host)
      .withHttpApp(httpApp)
      .resource
      .use(_ => IO(scala.io.StdIn.readLine()))
      .as(ExitCode.Success)
  }

}
