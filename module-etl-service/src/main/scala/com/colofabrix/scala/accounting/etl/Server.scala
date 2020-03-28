package com.colofabrix.scala.accounting.etl

import cats.effect.IO
import com.colofabrix.scala.accounting.etl.config._
import com.colofabrix.scala.accounting.utils.logging._
import org.http4s.server.{ Server => Http4sServer }
import scala.io.StdIn

object Server extends PureLogging {
  val logger = org.log4s.getLogger

  def main(server: Http4sServer[IO]): IO[_] =
    for {
      _ <- pureLogger.info[IO](s"Started ${BuildInfo.description} version ${BuildInfo.version}")
      _ <- pureLogger.trace[IO](server.toString)
      _ <- IO(if (serviceConfig.server.debugMode) StdIn.readLine() else ())
    } yield ()
}
