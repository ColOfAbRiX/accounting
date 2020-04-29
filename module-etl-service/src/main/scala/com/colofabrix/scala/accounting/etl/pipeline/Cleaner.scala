package com.colofabrix.scala.accounting.etl.pipeline

import cats.data.Nested
import cats.implicits._
import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.etl.refined.shapeless.RefinedPoly1
import com.colofabrix.scala.accounting.utils.logging._
import com.colofabrix.scala.accounting.utils.validation._
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
  // -------- START OF PLAYGROUND --------
  object defaultCleaner extends RefinedPoly1 {

    implicit def caseBigDecimal: ValidatedCase[BigDecimal] = at[BigDecimal](validIdentity)
    implicit def caseDouble: ValidatedCase[Double]         = at[Double](validIdentity)
    implicit def caseInt: ValidatedCase[Int]               = at[Int](validIdentity)
    implicit def caseLocalDate: ValidatedCase[LocalDate]   = at[LocalDate](validIdentity)
    implicit def caseString: ValidatedCase[String]         = at[String](trim combine toLowercase combine removeRedundantSpaces)

    implicit def caseSimpleOptionA[A]: ValidatedCase[Option[A]] =
      at[Option[A]](validIdentity)

    //import cats.Traverse

    //implicit def caseOptionInt(implicit baseCase: ValidatedCase[Int]): ValidatedCase[Option[Int]] =
    //  at[Option[Int]](Traverse[Option].traverse(_)(baseCase.apply))

    //implicit def caseOptionA[A](implicit baseCase: ValidatedCase[A]): ValidatedCase[Option[A]] =
    //  at[Option[A]](Traverse[Option].traverse(_)(baseCase.apply))

    //implicit def caseFA[F[_]: Traverse, A](implicit baseCase: ValidatedCase[A]): ValidatedCase[F[A]] =
    //  at[F[A]](Traverse[F].traverse(_)(baseCase.apply))
  }

  //import cats.implicits._
  //import shapeless._
  //
  //case class Test(oa: Option[Int])
  //private val test = Test(None)
  //
  //private val gen       = Generic[Test]
  //private val to        = gen.to(test)
  //private val cleaned   = to.map(defaultCleaner)
  //private val sequenced = cleaned.sequence
  //private val cleanTest = sequenced.map(x => gen.from(x))
  //private val result    = cleanTest.getOrElse(test)
  //
  //cats.effect.IO(println(result)).unsafeRunSync()
  //-------- END OF PLAYGROUND --------

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
