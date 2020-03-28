package com.colofabrix.scala.accounting.etl

import cats.effect.IO
import com.colofabrix.scala.accounting.etl.config._
import com.colofabrix.scala.accounting.utils.logging._
import org.http4s.server.{ Server => Http4sServer }
import scala.io.StdIn

object Server extends PureLogging[IO] {
  def main(server: Http4sServer[IO]): IO[_] =
    for {
      _ <- pureLogger.info(s"Started ${BuildInfo.description} version ${BuildInfo.version}")
      _ <- pureLogger.trace(server.toString)
      _ <- IO(if (serviceConfig.server.debugMode) StdIn.readLine() else ())
    } yield ()
}
