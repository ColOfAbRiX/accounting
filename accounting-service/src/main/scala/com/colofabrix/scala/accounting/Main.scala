package com.colofabrix.scala.accounting

import monix.eval.Task
import monix.reactive.Observable
import monix.execution.Scheduler.Implicits.global
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import model._

// object Main extends IOApp {
//   def run(args: List[String]) =
//     AccountingServer.stream[IO].compile.drain.as(ExitCode.Success)
// }

object Main extends App {

  val file = new java.io.File("samples/sample_barclays.csv")
  val reader = new KantanCsvReader
  val result = reader.readFile(file)

  println(s"Result: $result")
  result.foreach { observable =>
    println(s"Observable: $observable")
    println("Running for each element:")
    observable.foreachL(println).runToFuture
  }

  Thread.sleep(5000)
}
