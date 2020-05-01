package com.colofabrix.scala.accounting

import cats.effect._
import com.colofabrix.scala.accounting.transactions.api._
import com.colofabrix.scala.accounting.transactions.client._
import com.colofabrix.scala.accounting.transactions.config._
import com.colofabrix.scala.accounting.utils.logging._
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeServerBuilder
import scala.concurrent.ExecutionContext.global
import scala.io.StdIn

object AccountingTransactionsService extends IOApp with PureLogging {
  protected[this] val logger = org.log4s.getLogger

  private[this] val transactionsClient: TransactionsClient[IO]       = new TransactionsClientImpl
  private[this] val transactionsEndpoints: TransactionsEndpoints[IO] = new TransactionsEndpointsImpl(transactionsClient)

  def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- BlazeServerBuilder[IO](global)
            .bindHttp(serviceConfig.server.port, serviceConfig.server.host)
            .withHttpApp(transactionsEndpoints.app(contextShift))
            .resource
            .use(server)
    } yield ExitCode.Success

  private[this] def server(server: Server[IO]): IO[_] =
    for {
      _ <- pureLogger.info[IO](
            s"STARTED ${transactions.BuildInfo.description} version ${transactions.BuildInfo.version}",
          )
      _ <- pureLogger.trace[IO](server.toString)
      _ <- IO(if (serviceConfig.server.debugMode) StdIn.readLine() else ())
    } yield ()
}
