package com.colofabrix.scala.accounting.utils

import cats.Show

/**
 * Base type for Algebraic Data Types
 */
trait ADT extends Product with Serializable

object ADT {

  implicit def showAdt[T <: ADT]: Show[T] = new Show[T] {
    def show(t: T): String = pprint.apply(t).toString()
  }

}
