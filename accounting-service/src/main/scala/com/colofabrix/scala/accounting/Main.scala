package com.colofabrix.scala.accounting

import com.colofabrix.scala.accounting.model.BarclaysTransaction
import cats.data.Validated.Invalid
import cats.data.Validated.Valid

// object Main extends IOApp {
//   def run(args: List[String]) =
//     AccountingServer.stream[IO].compile.drain.as(ExitCode.Success)
// }

object Main extends App {

  import etl._
  import csv._
  import AllInputs._

  val csvReader = new FileCsvReader(new java.io.File("samples/sample_barclays.csv"))
  val converter = new CsvInputConverter[BarclaysTransaction](csvReader, barclaysCsvProc)
  val result    = converter.ingestInput

  result match {
    case Invalid(e) =>
      println("ERRORS")
      e.iterator.foreach(println)
    case Valid(transactions) =>
      println("TRANSACTIONS")
      transactions.foreach(println)
  }

}
