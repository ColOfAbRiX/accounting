package com.colofabrix.scala.accounting.etl.model

import cats.effect.IO
import com.colofabrix.scala.accounting.utils.ADT
import com.colofabrix.scala.accounting.utils.validation._
import sttp.tapir.Endpoint

object Api {

  /**
   * Sum type to represent API errors
   */
  sealed trait ErrorInfo                                          extends ADT
  final case class GenericException(message: String)              extends ErrorInfo
  final case class ValidationErrors(error: List[ValidationError]) extends ErrorInfo

  // TODO: Not sure if I need to specify an effect here
  type EtlEndpoint[I, O] = Endpoint[I, ErrorInfo, O, Nothing]

  // TODO: I don't really like this. See if I can use a transformer or if I can use a different name
  type ClientOutput[A] = IO[Either[ErrorInfo, A]]

}
