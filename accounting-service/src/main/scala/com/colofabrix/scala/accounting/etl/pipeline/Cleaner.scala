package com.colofabrix.scala.accounting.etl.pipeline

import cats.data.Nested
import cats.implicits._
import com.colofabrix.scala.accounting.etl.inputs._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation
import com.colofabrix.scala.accounting.utils.validation._
import shapeless.Generic
import java.time.LocalDate

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
        InputInstances.barclaysInput.clean(transaction)
    }

  implicit val halifaxCleaner: Cleaner[HalifaxTransaction] =
    new Cleaner[HalifaxTransaction] {
      def clean(transaction: HalifaxTransaction) =
        InputInstances.halifaxInput.clean(transaction)
    }

  implicit val starlingCleaner: Cleaner[StarlingTransaction] =
    new Cleaner[StarlingTransaction] {
      def clean(transaction: StarlingTransaction) =
        InputInstances.starlingInput.clean(transaction)
    }

  implicit val amexCleaner: Cleaner[AmexTransaction] =
    new Cleaner[AmexTransaction] {
      def clean(transaction: AmexTransaction) =
        InputInstances.amexInput.clean(transaction)
    }

}

/**
 * Utility functions for cleaning
 */
object CleanerUtils {
  import shapeless._

  @SuppressWarnings(Array("org.wartremover.warts.ExplicitImplicitTypes", "org.wartremover.warts.PublicInference"))
  object defaultCleaner extends Poly1 {
    implicit def caseString     = at[String](trim andThen toLowercase andThen removeRedundantSpaces)
    implicit def caseBigDecimal = at[BigDecimal](identity)
    implicit def caseLocalDate  = at[LocalDate](identity)
    implicit def caseOptionA[A] = at[Option[A]](identity)
  }

  /** Transforms a string field into lowercase */
  def toLowercase: String => String = _.toLowerCase()

  /** Reduces multiple spaces into one single space */
  def removeRedundantSpaces: String => String = _.replaceAll("\\s+", " ")

  /** Removes the punctuation from a string */
  def removePunctuation: String => String = _.replaceAll("""[\p{Punct}&&[^.]]""", "")

  /** Removes the punctuation from a string */
  def trim: String => String = _.trim

}
