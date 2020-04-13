package com.colofabrix.scala.accounting.transactions.model

import com.colofabrix.scala.accounting.utils.ADT

object Api {

  /**
   * Sum type to represent API errors
   */
  sealed trait ErrorInfo extends ADT {
    def message: String
  }
  final case class GenericExceptionError(message: String) extends ErrorInfo
  final case class UnknownError(message: String)          extends ErrorInfo

}
