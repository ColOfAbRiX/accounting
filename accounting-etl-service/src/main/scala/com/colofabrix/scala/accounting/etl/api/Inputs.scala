package com.colofabrix.scala.accounting.etl.api

import com.colofabrix.scala.accounting.etl.api.Codecs._
import com.colofabrix.scala.accounting.model._
import sttp.tapir._

/**
 * Inputs are the values that can be accepted by an endpoint
 */
object Inputs {

  val recordTypeQuery: EndpointInput[InputType] =
    query[InputType]("recordType")
      .description("Type of record to convert")
      .example(BarclaysInputType)

}
