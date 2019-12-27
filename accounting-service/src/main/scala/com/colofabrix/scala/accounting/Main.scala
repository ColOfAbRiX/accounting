package com.colofabrix.scala.accounting

import cats.data.Validated.{ Invalid, Valid }
import com.colofabrix.scala.accounting.model.BarclaysTransaction
import cats.effect._
import java.util.concurrent.ThreadPoolExecutor

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
    val result = implicitly[CsvProcessor[BarclaysTransaction]].process[IO](csvReader.read)
    result.map { x => println(x); x }.compile.toVector.unsafeRunSync()

    ExitCode.Success
  }

}

object ExecutorsBuilder {

  // https://blog.jessitron.com/2014/01/29/choosing-an-executorservice/
  val coresCount = Runtime.getRuntime().availableProcessors()

}
