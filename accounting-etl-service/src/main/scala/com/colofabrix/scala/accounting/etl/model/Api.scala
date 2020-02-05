package com.colofabrix.scala.accounting.etl.model

import com.colofabrix.scala.accounting.utils.ADT
import sttp.tapir.Endpoint
import sttp.model.StatusCode

object Api {

  /**
   * Sum type to represent API errors
   */
  sealed trait ErrorInfo                            extends ADT
  final case class ServerException(message: String) extends ErrorInfo
  final case class NotFound(message: String)        extends ErrorInfo
  final case class WrongRecordType(message: String) extends ErrorInfo

  type EtlApiError = (StatusCode, ErrorInfo)

  type EtlEndpoint[I, O] = Endpoint[I, EtlApiError, O, Nothing]

}
