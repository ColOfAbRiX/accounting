package com.colofabrix.scala.accounting.transactions

import cats.effect.IO
import com.colofabrix.scala.accounting.transactions.config._
import org.http4s.server.{ Server => Http4sServer }
import scala.io.StdIn

object Server {
  private[this] val logger = org.log4s.getLogger

  def main(server: Http4sServer[IO]): IO[_] = IO {
    logger.info(s"Started ${BuildInfo.description} version ${BuildInfo.version}")
    logger.trace(server.toString)

    if (serviceConfig.server.debugMode) {
      StdIn.readLine()
    } else {
      ()
    }
  }
}
