package com.colofabrix.scala.accounting.etl.api

import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.model._

object Model {

  sealed trait APIRequest
  final case class ConvertRecordRequest(record: RawRecord, recordType: InputType)     extends APIRequest
  final case class ConvertInputRequest(input: List[RawRecord], recordType: InputType) extends APIRequest

  sealed trait APIResponse
  final case class SupportedInputsResponse(inputs: List[InputType]) extends APIResponse
  final case class ConvertedRecordResponse(inputs: List[InputType]) extends APIResponse

}
