package com.colofabrix.scala.accounting

import cats.effect._
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.syntax.kleisli._
import sttp.tapir.server.http4s._

import scala.concurrent.ExecutionContext
import com.colofabrix.scala.accounting.etl.api._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.pipeline._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.ExecutionContexts

import com.colofabrix.scala.accounting.etl.pipeline.Normalizer._

@SuppressWarnings(Array("org.wartremover.warts.All"))
object MultipleEndpointsDocumentationHttp4sServer extends App {

  implicit val ec: ExecutionContext           = ExecutionContexts.computePool
  implicit val contextShift: ContextShift[IO] = IO.contextShift(ec)
  implicit val timer: Timer[IO]               = IO.timer(ec)

  val listSupportedInputsRoutes: HttpRoutes[IO] = EtlApiEndpoints
    .listSupportedInputs
    .toRoutes { _ =>
      IO("listSupportedInputsRoutes is working!".asRight[EtlApiEndpoints.ErrorOutput])
    }

  val convertRecordRoutes: HttpRoutes[IO] = EtlApiEndpoints
    .convertRecord
    .toRoutes {
      case (inputType, body) =>
        val record: RawInput[IO] = fs2.Stream.emit(List(body))
        val converted = inputType match {
          case BarclaysInputType =>
            println(record)
            val result = Pipeline.fromCsv[String, BarclaysTransaction](body)
            result.map { x =>
              println(x)
              x
            }
          case HalifaxInputType  => Pipeline.fromStream[IO, HalifaxTransaction](record)
          case StarlingInputType => Pipeline.fromStream[IO, StarlingTransaction](record)
          case AmexInputType     => Pipeline.fromStream[IO, AmexTransaction](record)
        }
        IO {
          converted.compile.toList.unsafeRunSync.toString.asRight[EtlApiEndpoints.ErrorOutput]
        }
    }

  val convertRecordsRoutes: HttpRoutes[IO] = EtlApiEndpoints
    .convertRecords
    .toRoutes {
      case (inputType, body) =>
        val record: RawInput[IO] = fs2.Stream.emit(List(body))
        val converted = inputType match {
          case BarclaysInputType => Pipeline.fromStream[IO, BarclaysTransaction](record)
          case HalifaxInputType  => Pipeline.fromStream[IO, HalifaxTransaction](record)
          case StarlingInputType => Pipeline.fromStream[IO, StarlingTransaction](record)
          case AmexInputType     => Pipeline.fromStream[IO, AmexTransaction](record)
        }
        IO {
          converted.compile.toList.unsafeRunSync.toString.asRight[EtlApiEndpoints.ErrorOutput]
        }
    }

  val routes: HttpRoutes[IO] = listSupportedInputsRoutes <+> convertRecordRoutes <+> convertRecordsRoutes

  // starting the server
  BlazeServerBuilder[IO]
    .bindHttp(8080, "localhost")
    .withHttpApp(Router("/" -> routes).orNotFound)
    .resource
    .use { _ =>
      IO {
        scala.io.StdIn.readLine()
      }
    }
    .unsafeRunSync()
}

// import cats.effect._
// import com.colofabrix.scala.accounting.etl.pipeline._
// import com.colofabrix.scala.accounting.etl.pipeline.Normalizer._
// import com.colofabrix.scala.accounting.model._

// object Main extends IOApp {

//   def run(args: List[String]): IO[ExitCode] = IO.pure {
//     // The "samples" directory is omitted on purpose from the repo to use it as dirty working directory
//     val barclays = Pipeline.fromCsvPath[BarclaysTransaction]("samples/sample_barclays.csv")
//     val halifax  = Pipeline.fromCsvPath[HalifaxTransaction]("samples/sample_halifax.csv")
//     val starling = Pipeline.fromCsvPath[StarlingTransaction]("samples/sample_starling.csv")
//     val amex     = Pipeline.fromCsvPath[AmexTransaction]("samples/sample_amex.csv")

//     val result = for {
//       inputsV     <- barclays append halifax append starling append amex
//       transaction <- inputsV.fold(_ => fs2.Stream.empty, fs2.Stream(_))
//     } yield {
//       transaction
//     }

//     result
//       .compile
//       .toList
//       .unsafeRunSync()
//       .foreach(println)

//     ExitCode.Success
//   }

// }
