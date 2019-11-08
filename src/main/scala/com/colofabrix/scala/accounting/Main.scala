package com.colofabrix.scala.accounting

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

object Main extends IOApp {
  def run(args: List[String]) =
    AccountingServer.stream[IO].compile.drain.as(ExitCode.Success)
}