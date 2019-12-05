package com.colofabrix.scala.accounting

import com.colofabrix.scala.accounting.banks.Starling.StarlingCsvFile
import com.colofabrix.scala.accounting.banks.Halifax.HalifaxCsvFile
import com.colofabrix.scala.accounting.banks.Barclays.BarclaysCsvFile
import com.colofabrix.scala.accounting.banks.Amex.AmexCsvFile
import com.colofabrix.scala.accounting.csv.CsvDefinitions._
import com.colofabrix.scala.accounting.csv._
import monix.execution.Scheduler.Implicits.global
import monix.reactive.Observable

// object Main extends IOApp {
//   def run(args: List[String]) =
//     AccountingServer.stream[IO].compile.drain.as(ExitCode.Success)
// }

object Main extends App {

  val file = new java.io.File("samples/sample_halifax.csv")
  val reader = CsvReaderType(KantanCsvReaderType)
  val result = reader.readFile(file)

  // Print output
  result.foreach { observable =>
    val result = for {
      row         <- InputCleaning.cleanFile(observable)
      transaction <- Observable(HalifaxCsvFile.convertRow(row))
    } yield {
      transaction
    }

    result.foreachL(println).runToFuture
  }

  Thread.sleep(3000)
}
