package com.colofabrix.scala.accounting

import cats.effect._
import com.colofabrix.scala.accounting.model._
import etl._
import etl.pipeline._
import csv._
import Transformer._
import cats.data.Validated.Valid

// object Main extends IOApp {
//   def run(args: List[String]) =
//     AccountingServer.stream[IO].compile.drain.as(ExitCode.Success)
// }

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = IO.pure {
    val barclays = Loader
      .fromCsvPath[BarclaysTransaction]("samples/sample_barclays.csv")
      .through(Transformer[BarclaysTransaction])

    val halifax = Loader
      .fromCsvPath[HalifaxTransaction]("samples/sample_halifax.csv")
      .through(Transformer[HalifaxTransaction])

    val starling = Loader
      .fromCsvPath[StarlingTransaction]("samples/sample_starling.csv")
      .through(Transformer[StarlingTransaction])

    val amex = Loader
      .fromCsvPath[AmexTransaction]("samples/sample_amex.csv")
      .through(Transformer[AmexTransaction])

    val result = for {
      inputsV     <- barclays append halifax append starling append amex
      transaction <- inputsV.fold(_ => fs2.Stream.empty, fs2.Stream(_))
    } yield {
      transaction
    }

    result
      .compile
      .toList
      .unsafeRunSync()
      .foreach(println)

    ExitCode.Success
  }

}
