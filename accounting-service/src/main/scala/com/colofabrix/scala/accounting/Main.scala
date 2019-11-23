package com.colofabrix.scala.accounting

import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.model.Barclays._
import com.colofabrix.scala.accounting.csv.CsvDefinitions.CsvReaderType
import com.colofabrix.scala.accounting.csv.KantanCsvReaderType
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
      row <- InputCleaning.cleanFile(observable).drop(1)
    } yield {
      row
    }

    val transactions = result.map{ row =>
      print(s"Row: $row")
      BarclaysCsvFile.convertRow(row)
    }

    transactions.foreachL(println).runToFuture
  }

  Thread.sleep(3000)
}
