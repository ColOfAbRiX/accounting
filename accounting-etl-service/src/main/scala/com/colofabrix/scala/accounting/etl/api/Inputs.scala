package com.colofabrix.scala.accounting.etl.api

import com.colofabrix.scala.accounting.etl.api.InputCodecs._
import com.colofabrix.scala.accounting.etl.model.Config._
import sttp.tapir._

/**
 * Inputs are the values that can be accepted by an endpoint
 */
object Inputs {

  /**
   * An input representing the type of record
   */
  val inputTypeQuery: EndpointInput[InputType] =
    query[InputType]("inputType")
      .description("The type of input to convert")
      .example(BarclaysInputType)

}
