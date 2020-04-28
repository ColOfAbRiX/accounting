package com.colofabrix.scala.accounting.etl.pipeline

import cats.data.Nested
import cats.implicits._
import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.etl.refined.shapeless.RefinedPoly1
import com.colofabrix.scala.accounting.utils.logging._
import com.colofabrix.scala.accounting.utils.validation.streams._
import java.time.LocalDate

/**
 * Cleans the individual fields of the InputTransactions
 */
trait Cleaner[T <: InputTransaction] {
  def cleanInputTransaction(transaction: T): T
}

object Cleaner extends PipeLogging {
  protected[this] val logger = org.log4s.getLogger

  /**
   * Cleans a stream of InputTransaction
   */
  def apply[F[_], T <: InputTransaction](implicit c: Cleaner[T]): VPipe[F, T, T] = {
    val log: VPipe[F, T, T] = pipeLogger.trace(x => s"Cleaning transaction: ${x.toString}")
    val clean: VPipe[F, T, T] =
      Nested(_)
        .map(c.cleanInputTransaction)
        .value

    log andThen clean
  }
}

/**
 * Utility functions for cleaning
 */
object CleanerUtils {
  @SuppressWarnings(Array("org.wartremover.warts.ExplicitImplicitTypes", "org.wartremover.warts.PublicInference"))
  object defaultCleaner extends RefinedPoly1 {
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

  /** Removes leading and trailing spaces */
  def trim: String => String = _.trim
}
