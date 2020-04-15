package com.colofabrix.scala.accounting

import cats.effect._
import com.colofabrix.scala.accounting.etl.api._
import com.colofabrix.scala.accounting.etl.client._
import com.colofabrix.scala.accounting.etl.config._
import com.colofabrix.scala.accounting.utils.logging._
import eu.timepit.refined.auto._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.Server
import scala.io.StdIn

object AccountingEtlService extends IOApp with PureLogging {
  protected[this] val logger = org.log4s.getLogger

  private[this] val etlClient: EtlClient[IO]       = new EtlClientImpl(contextShift)
  private[this] val etlEndpoints: EtlEndpoints[IO] = new EtlEndpointsImpl(etlClient)

  def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- BlazeServerBuilder[IO]
            .bindHttp(serviceConfig.server.port, serviceConfig.server.host)
            .withHttpApp(etlEndpoints.app(contextShift))
            .resource
            .use(server)
    } yield ExitCode.Success

  private[this] def server(server: Server[IO]): IO[_] =
    for {
      _ <- pureLogger.info[IO](s" *** Started ${etl.BuildInfo.description} version ${etl.BuildInfo.version} ***")
      _ <- pureLogger.trace[IO](server.toString)
      _ <- IO(if (serviceConfig.server.debugMode) StdIn.readLine() else ())
    } yield ()
}
