package com.colofabrix.scala.accounting

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.etl.Server
import com.colofabrix.scala.accounting.etl.api._
import com.colofabrix.scala.accounting.etl.config._
import org.http4s.{ HttpApp, HttpRoutes }
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.syntax.kleisli._

object AccountingEtlService extends IOApp {

  private[this] def httpApp: HttpApp[IO] =
    Router(
      "/"     -> Routes[IO].allRoutes.foldLeft(HttpRoutes.empty[IO])(_ <+> _),
      "/docs" -> Routes[IO].docsRoute,
    ).orNotFound

  override def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(serviceConfig.server.port, serviceConfig.server.host)
      .withHttpApp(httpApp)
      .resource
      .use(Server.main)
      .as(ExitCode.Success)

}
