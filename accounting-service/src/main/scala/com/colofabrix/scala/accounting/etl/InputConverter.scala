package com.colofabrix.scala.accounting.etl

import com.colofabrix.scala.accounting.model.InputTransaction
import com.colofabrix.scala.accounting.utils.AValidation._

/**
 * Converts an input into transactions
 */
trait InputConverter[T <: InputTransaction] {
  def ingestInput: AValidated[List[T]]
}
