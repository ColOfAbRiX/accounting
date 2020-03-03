package com.colofabrix.scala.accounting.transactions.model

import com.colofabrix.scala.accounting.utils._

object Api {

  /**
   * Sum type to represent API errors
   */
  sealed trait ErrorInfo                             extends ADT
  final case class GenericException(message: String) extends ErrorInfo

}
