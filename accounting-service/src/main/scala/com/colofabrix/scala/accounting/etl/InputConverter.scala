package com.colofabrix.scala.accounting.etl

import com.colofabrix.scala.accounting.model.InputTransaction
import com.colofabrix.scala.accounting.utils.validation._
import com.colofabrix.scala.accounting.etl.definitions._
import cats.effect.IO

/**
 * Converts an input into transactions
 */
trait InputConverter[+T <: InputTransaction] {
  def ingestInput: VStream[IO, T]
}
