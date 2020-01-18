package com.colofabrix.scala.accounting

import cats.effect._
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.Router
import org.http4s.syntax.kleisli._
import sttp.tapir.server.http4s._

import scala.concurrent.ExecutionContext
import com.colofabrix.scala.accounting.etl.api._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.pipeline._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.ExecutionContexts
import com.colofabrix.scala.accounting.etl.ApiPipelineInstances._

@SuppressWarnings(Array("org.wartremover.warts.All"))
object MultipleEndpointsDocumentationHttp4sServer extends App {

  implicit val ec: ExecutionContext           = ExecutionContexts.computePool
  implicit val contextShift: ContextShift[IO] = IO.contextShift(ec)
  implicit val timer: Timer[IO]               = IO.timer(ec)

  val listSupportedInputsRoutes: HttpRoutes[IO] = Endpoints
    .listSupportedInputs
    .toRoutes { _ =>
      IO("listSupportedInputsRoutes is working!".asRight[Endpoints.ErrorOutput])
    }

  val convertRecordRoutes: HttpRoutes[IO] = Endpoints
    .convertRecord
    .toRoutes {
      case (inputType, body) =>
        val converted = inputType match {
          case BarclaysInputType => Pipeline.fromCsv[String, BarclaysTransaction](body).head
          case HalifaxInputType  => Pipeline.fromCsv[String, HalifaxTransaction](body).head
          case StarlingInputType => Pipeline.fromCsv[String, StarlingTransaction](body).head
          case AmexInputType     => Pipeline.fromCsv[String, AmexTransaction](body).head
        }
        IO {
          converted.compile.toList.unsafeRunSync.toString.asRight[Endpoints.ErrorOutput]
        }
    }

  val convertRecordsRoutes: HttpRoutes[IO] = Endpoints
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
          converted.compile.toList.unsafeRunSync.toString.asRight[Endpoints.ErrorOutput]
        }
    }

  val routes = List(
    listSupportedInputsRoutes,
    convertRecordRoutes,
    convertRecordsRoutes,
    Routes.redocRoute.routes[IO],
  ).reduce(_ <+> _)

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
