package com.colofabrix.scala.accounting.etl.api

import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.model._
import EtlApiCodecs._
import EtlApiInputs._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.Codec._
import sttp.tapir.json.circe._

object EtlApiModel {

  sealed trait APIRequest
  final case class ConvertRecordRequest(record: RawRecord, recordType: InputType)     extends APIRequest
  final case class ConvertInputRequest(input: List[RawRecord], recordType: InputType) extends APIRequest

  sealed trait APIResponse
  final case class SupportedInputsResponse(inputs: List[InputType]) extends APIResponse
  final case class ConvertedRecordResponse(inputs: List[InputType]) extends APIResponse

}

object EtlApiCodecs {

  implicit val inputTypeCodec: PlainCodec[InputType] = {
    val code: PartialFunction[String, InputType] = {
      case "barclays" => BarclaysInputType
      case "halifax"  => HalifaxInputType
      case "starling" => StarlingInputType
      case "amex"     => AmexInputType
    }
    val decode: PartialFunction[InputType, String] = {
      case BarclaysInputType => "barclays"
      case HalifaxInputType  => "halifax"
      case StarlingInputType => "starling"
      case AmexInputType     => "amex"
    }
    implicitly[PlainCodec[String]].map(code)(decode)
  }

}

object EtlApiInputs {

  val recordTypeQuery: EndpointInput[InputType] = query[InputType]("recordType")

}

object EtlApiEndpoints {

  type ErrorInfo   = String
  type ErrorOutput = (StatusCode, ErrorInfo)

  val baseEndpoint: Endpoint[Unit, ErrorOutput, Unit, Nothing] = endpoint
    .in("api" / "v1.0")
    .errorOut(statusCode and jsonBody[ErrorInfo])

  val listSupportedInputs: Endpoint[Unit, ErrorOutput, String, Nothing] =
    baseEndpoint
      .get
      .in("supported-inputs")
      .out(stringBody)

  val convertRecord: Endpoint[InputType, ErrorOutput, String, Nothing] =
    baseEndpoint
      .get
      .in("convert-record")
      .in(recordTypeQuery)
      .out(stringBody)

  val convertRecords: Endpoint[(InputType, String), ErrorOutput, String, Nothing] =
    baseEndpoint
      .get
      .in("convert-records")
      .in(recordTypeQuery)
      .in(stringBody)
      .out(stringBody)

}
