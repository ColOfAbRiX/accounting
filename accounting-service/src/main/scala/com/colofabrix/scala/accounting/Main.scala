package com.colofabrix.scala.accounting

import com.colofabrix.scala.accounting.banks.Barclays.BarclaysCsvFile
import com.colofabrix.scala.accounting.csv.CsvDefinitions.CsvReaderType
import com.colofabrix.scala.accounting.csv.{InputCleaning, KantanCsvReaderType}
import monix.execution.Scheduler.Implicits.global

// object Main extends IOApp {
//   def run(args: List[String]) =
//     AccountingServer.stream[IO].compile.drain.as(ExitCode.Success)
// }

object Main extends App {

  val file = new java.io.File("samples/sample_barclays.csv")
  val reader = CsvReaderType(KantanCsvReaderType)
  val result = reader.readFile(file)

  // Print output
  result.foreach { observable =>
    val result = for {
      row <- InputCleaning.cleanFile(observable)
    } yield {
      row
    }

    val transactions = result.map{ row =>
      BarclaysCsvFile.convertRow(row)
    }

    transactions.foreachL(println).runToFuture
  }

  Thread.sleep(3000)
}
