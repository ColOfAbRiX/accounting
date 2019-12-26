package com.colofabrix.scala.accounting.etl

import com.colofabrix.scala.accounting.model.InputTransaction
import com.colofabrix.scala.accounting.utils.validation._
import com.colofabrix.scala.accounting.etl.definitions._

/**
 * Converts an input into transactions
 */
trait InputConverter[+T <: InputTransaction] {
  def ingestInput: BankInputsV[T]
}
