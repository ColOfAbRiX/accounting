package com.colofabrix.scala.accounting.etl

import com.colofabrix.scala.accounting.model.InputTransaction
import com.colofabrix.scala.accounting.utils.AValidation._

/**
 * Converts an input into transactions
 */
trait InputConverter[I, T <: InputTransaction] {
  def ingestInput(input: I): AValidated[List[T]]
}
