package com.colofabrix.scala.accounting

import cats.effect._
import com.colofabrix.scala.accounting.etl.pipeline._
import com.colofabrix.scala.accounting.etl.pipeline.Normalizer._
import com.colofabrix.scala.accounting.model._

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = IO.pure {
    val barclays = Pipeline.fromCsvPath[BarclaysTransaction]("samples/sample_barclays.csv")
    val halifax  = Pipeline.fromCsvPath[HalifaxTransaction]("samples/sample_halifax.csv")
    val starling = Pipeline.fromCsvPath[StarlingTransaction]("samples/sample_starling.csv")
    val amex     = Pipeline.fromCsvPath[AmexTransaction]("samples/sample_amex.csv")

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
