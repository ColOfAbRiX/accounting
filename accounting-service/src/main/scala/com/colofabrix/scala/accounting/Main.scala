package com.colofabrix.scala.accounting

import cats.effect._
import com.colofabrix.scala.accounting.model._
import etl._
import csv._

// object Main extends IOApp {
//   def run(args: List[String]) =
//     AccountingServer.stream[IO].compile.drain.as(ExitCode.Success)
// }

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = IO.pure {
    val barclays = Loader.fromCsvPath[BarclaysTransaction]("samples/sample_barclays.csv")
    val halifax  = Loader.fromCsvPath[HalifaxTransaction]("samples/sample_halifax.csv")
    val starling = Loader.fromCsvPath[StarlingTransaction]("samples/sample_starling.csv")
    val amex     = Loader.fromCsvPath[AmexTransaction]("samples/sample_amex.csv")

    (barclays append halifax append starling append amex)
      .compile
      .toList
      .unsafeRunSync()
      .foreach(println)

    ExitCode.Success
  }

}
