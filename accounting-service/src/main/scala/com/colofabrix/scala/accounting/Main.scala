package com.colofabrix.scala.accounting

import cats.data.Validated.{ Invalid, Valid }
import com.colofabrix.scala.accounting.model.BarclaysTransaction
import cats.effect._

// object Main extends IOApp {
//   def run(args: List[String]) =
//     AccountingServer.stream[IO].compile.drain.as(ExitCode.Success)
// }

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = IO.pure {
    import etl._
    import csv._
    import AllInputs._

    val csvReader = new FileCsvReader(new java.io.File("samples/sample_barclays.csv"))
    val result = implicitly[CsvProcessor[BarclaysTransaction]].process(csvReader.read)
    result.map { x => println(x); x }.compile.toVector.unsafeRunSync()

    ExitCode.Success
  }

}
