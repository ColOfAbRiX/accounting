package com.colofabrix.scala.accounting.etl.pipeline

import com.colofabrix.scala.accounting.utils.validation._
import com.colofabrix.scala.accounting.model.InputTransaction
import com.colofabrix.scala.accounting.model.BarclaysTransaction
import com.colofabrix.scala.accounting.utils.validation
import cats.data.Nested
import cats.implicits._
import com.colofabrix.scala.accounting.etl.inputs.BarclaysInputProcessor

/**
 * Cleans the individual fields of the InputTransactions
 */
trait Cleaner[T <: InputTransaction] {
  def clean(transaction: T): T
}

object Cleaner {}
