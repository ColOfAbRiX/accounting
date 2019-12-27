package com.colofabrix.scala.accounting

import cats.data.Validated.{ Invalid, Valid }
import com.colofabrix.scala.accounting.model.BarclaysTransaction
import cats.effect._

// object Main extends IOApp {
//   def run(args: List[String]) =
//     AccountingServer.stream[IO].compile.drain.as(ExitCode.Success)
// }

object Main extends IOApp {

  import scala.concurrent.ExecutionContext
  import java.util.concurrent.Executors

  def run(args: List[String]): IO[ExitCode] = IO.pure {
    import etl._
    import csv._
    import AllInputs._

    val csvReader = new FileCsvReader(new java.io.File("samples/sample_barclays.csv"))
    val stream = csvReader.read.map { x => println(x); x }

    stream.compile.toVector.unsafeRunSync()

    // val converter = new CsvInputConverter[BarclaysTransaction](csvReader, barclaysCsvProc)
    // val result    = converter.ingestInput

    // result match {
    //   case Invalid(e) =>
    //     println("ERRORS")
    //     e.iterator.foreach(println)
    //   case Valid(transactions) =>
    //     println("TRANSACTIONS")
    //     transactions.foreach(println)
    // }

    ExitCode.Success
  }

}
