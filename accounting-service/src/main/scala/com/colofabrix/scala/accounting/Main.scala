package com.colofabrix.scala.accounting

import cats.effect._
import com.colofabrix.scala.accounting.model._
import etl._
import etl.pipeline._
import Normalizer._
import cats.data.Validated.Valid

// object Main extends IOApp {
//   def run(args: List[String]) =
//     AccountingServer.stream[IO].compile.drain.as(ExitCode.Success)
// }

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = IO.pure {
    val barclays = InputProcessor
      .fromCsvPath[BarclaysTransaction]("samples/sample_barclays.csv")
      .through(Cleaner[BarclaysTransaction])
      .through(Normalizer[BarclaysTransaction])

    val halifax = InputProcessor
      .fromCsvPath[HalifaxTransaction]("samples/sample_halifax.csv")
      .through(Cleaner[HalifaxTransaction])
      .through(Normalizer[HalifaxTransaction])

    val starling = InputProcessor
      .fromCsvPath[StarlingTransaction]("samples/sample_starling.csv")
      .through(Cleaner[StarlingTransaction])
      .through(Normalizer[StarlingTransaction])

    val amex = InputProcessor
      .fromCsvPath[AmexTransaction]("samples/sample_amex.csv")
      .through(Cleaner[AmexTransaction])
      .through(Normalizer[AmexTransaction])

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
