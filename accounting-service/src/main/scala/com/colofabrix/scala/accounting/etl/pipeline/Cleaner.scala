package com.colofabrix.scala.accounting.etl.pipeline

import cats.data.Nested
import cats.implicits._
import com.colofabrix.scala.accounting.etl.inputs._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation
import com.colofabrix.scala.accounting.utils.validation._

/**
 * Cleans the individual fields of the InputTransactions
 */
trait Cleaner[T <: InputTransaction] {
  def clean(transaction: T): T
}

object Cleaner {

  def apply[T <: InputTransaction](implicit C: Cleaner[T]): VPipe[fs2.Pure, T, T] = { input =>
    Nested(input)
      .map(C.clean)
      .value
  }

  implicit val barclaysCleaner: Cleaner[BarclaysTransaction] =
    new Cleaner[BarclaysTransaction] {
      def clean(transaction: BarclaysTransaction) =
        new BarclaysInputProcessor().clean(transaction)
    }

  implicit val halifaxCleaner: Cleaner[HalifaxTransaction] =
    new Cleaner[HalifaxTransaction] {
      def clean(transaction: HalifaxTransaction) =
        new HalifaxInputProcessor().clean(transaction)
    }

  implicit val starlingCleaner: Cleaner[StarlingTransaction] =
    new Cleaner[StarlingTransaction] {
      def clean(transaction: StarlingTransaction) =
        new StarlingInputProcessor().clean(transaction)
    }

  implicit val amexCleaner: Cleaner[AmexTransaction] =
    new Cleaner[AmexTransaction] {
      def clean(transaction: AmexTransaction) =
        new AmexInputProcessor().clean(transaction)
    }

}
