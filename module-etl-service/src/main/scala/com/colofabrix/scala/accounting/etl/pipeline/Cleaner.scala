package com.colofabrix.scala.accounting.etl.pipeline

import cats.implicits._
import cats.sequence._
import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.etl.refined.shapeless.RefinedPoly1
import com.colofabrix.scala.accounting.utils.logging._
import com.colofabrix.scala.accounting.utils.validation._
import com.colofabrix.scala.accounting.utils.validation.streams._
import java.time.LocalDate
import shapeless.ops.hlist.Mapper
import shapeless.{ Generic, HList }
import simulacrum._

/**
 * Cleans the individual fields of the InputTransactions
 */
@typeclass trait Cleaner[T <: InputTransaction] {
  def cleanInputTransaction(transaction: T): AValidated[T]

  // format: off
  protected def genericCleaner[
    HT <: HList,
    HVT <: HList](
      cleaner: RefinedPoly1)(
      transaction: T)(
        implicit
        gen: Generic.Aux[T, HT],
        map: Mapper.Aux[cleaner.type, HT, HVT],
        seq: Sequencer.Aux[HVT, AValidated, HT],
  ): AValidated[T] = {
    val to       = gen.to(transaction)
    val cleaned  = to.map(cleaner)
    val vCleaned = cleaned.sequence
    val from     = vCleaned.map(gen.from)
    from
  }
  // format: on
}

object Cleaner extends PipeLogging {
  protected[this] val logger = org.log4s.getLogger

  /**
   * Cleans a stream of InputTransaction
   */
  def apply[F[_], T <: InputTransaction](implicit c: Cleaner[T]): VPipe[F, T, T] = {
    val log: VPipe[F, T, T] = pipeLogger.trace(x => s"Cleaning transaction: ${x.toString}")
    val clean: VPipe[F, T, T] = {
      _.map(_ andThen c.cleanInputTransaction)
    }

    log andThen clean
  }
}

/**
 * Utility functions for cleaning
 */
object CleanerUtils {
  object defaultCleaner extends RefinedPoly1 {
    implicit def caseBigDecimal: ValidatedCase[BigDecimal] = at[BigDecimal](validIdentity)
    implicit def caseDouble: ValidatedCase[Double]         = at[Double](validIdentity)
    implicit def caseInt: ValidatedCase[Int]               = at[Int](validIdentity)
    implicit def caseLocalDate: ValidatedCase[LocalDate]   = at[LocalDate](validIdentity)
    implicit def caseString: ValidatedCase[String]         = at[String](trim combine toLowercase combine removeRedundantSpaces)
  }

  /** Removes leading and trailing spaces */
  def validIdentity[A]: A => AValidated[A] = _.aValid

  /** Transforms a string field into lowercase */
  def toLowercase: String => AValidated[String] = _.toLowerCase().aValid

  /** Reduces multiple spaces into one single space */
  def removeRedundantSpaces: String => AValidated[String] = _.replaceAll("\\s+", " ").aValid

  /** Removes the punctuation from a string */
  def removePunctuation: String => AValidated[String] = _.replaceAll("""[\p{Punct}&&[^.]]""", "").aValid

  /** Removes leading and trailing spaces */
  def trim: String => AValidated[String] = _.trim.aValid
}
