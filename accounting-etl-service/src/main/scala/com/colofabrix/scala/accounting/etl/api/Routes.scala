package com.colofabrix.scala.accounting.etl.api

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.etl.api._
import com.colofabrix.scala.accounting.etl.ApiPipelineInstances._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.pipeline._
import com.colofabrix.scala.accounting.model._
import org.http4s.HttpRoutes
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.redoc.http4s.RedocHttp4s
import sttp.tapir.server.http4s._

@SuppressWarnings(Array("org.wartremover.warts.All"))
object Routes {

  def listSupportedInputs = IO {
    "listSupportedInputsRoutes is working!".asRight[Endpoints.ErrorOutput]
  }
  // val listSupportedInputsRoute: HttpRoutes[IO] = Endpoints.listSupportedInputs.toRoutes(listSupportedInputs)

  def convertRecord(inputType: InputType, body: String) = IO {
    val converted = inputType match {
      case BarclaysInputType => Pipeline.fromCsv[String, BarclaysTransaction](body).head
      case HalifaxInputType  => Pipeline.fromCsv[String, HalifaxTransaction](body).head
      case StarlingInputType => Pipeline.fromCsv[String, StarlingTransaction](body).head
      case AmexInputType     => Pipeline.fromCsv[String, AmexTransaction](body).head
    }
    converted.compile.toList.unsafeRunSync.toString.asRight[Endpoints.ErrorOutput]
  }
  // val convertRecordRoute = Endpoints.convertRecord.toRoutes(convertRecord)

  def convertRecords(inputType: InputType, body: String) = IO {
    val record: RawInput[IO] = fs2.Stream.emit(List(body))
    val converted = inputType match {
      case BarclaysInputType => Pipeline.fromStream[IO, BarclaysTransaction](record)
      case HalifaxInputType  => Pipeline.fromStream[IO, HalifaxTransaction](record)
      case StarlingInputType => Pipeline.fromStream[IO, StarlingTransaction](record)
      case AmexInputType     => Pipeline.fromStream[IO, AmexTransaction](record)
    }
    converted.compile.toList.unsafeRunSync.toString.asRight[Endpoints.ErrorOutput]
  }
  // val convertRecordsRoute = Endpoints.convertRecords.toRoutes(convertRecords)

  val redocDocs: RedocHttp4s = {
    new RedocHttp4s("Accounting ETL API", Endpoints.openApiDocsEndpoint.toYaml)
  }
  // val redocDocsRoute = Endpoints.redocDocs.toRoutes(redocRoute)

  def allRoutes(implicit cs: ContextShift[IO]): HttpRoutes[IO] =
    List[HttpRoutes[IO]](
      Endpoints.listSupportedInputs.toRoutes { _ =>
        listSupportedInputs
      },
      // Endpoints.convertRecord.toRoutes(convertRecord()),
      Endpoints.convertRecord.toRoutes {
        case (inputType, body) => convertRecord(inputType, body)
      },
      Endpoints.convertRecords.toRoutes {
        case (inputType, body) => convertRecords(inputType, body)
      },
      redocDocs.routes,
    ).reduce(_ <+> _)

}
