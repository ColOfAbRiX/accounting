package com.colofabrix.scala.accounting.etl.conversion

import cats._
import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.utils.validation._
import fs2.Stream

/**
 * Reader from Iterable
 */
class IterableReader(input: Iterable[RawRecord]) {
  private[this] val logger = org.log4s.getLogger

  def read[F[_]: Monad]: VRawInput[F] = {
    logger.debug("Reading input from Iterable reader")
    Stream.unfold(input.iterator)(i => if (i.hasNext) Some((i.next.aValid, i)) else None)
  }
}

/**
 * Reader of generic CSV
 */
class CsvReader[A: kantan.csv.CsvSource](input: A) {
  import kantan.csv._
  import kantan.csv.ops._

  private[this] type KantanReader = kantan.csv.CsvReader[ReadResult[List[String]]]
  private[this] val logger = org.log4s.getLogger

  def read[F[_]](cs: ContextShift[F])(implicit S: Sync[F]): VRawInput[F] = {
    val openReader = for {
      _      <- cs.shift
      reader <- S.pure(input.asCsvReader[List[String]](rfc))
      _      <- S.pure(logger.trace(s"Opened CSV reader ${reader.toString}"))
    } yield reader

    val closeReader = (r: KantanReader) =>
      for {
        _ <- cs.shift
        _ <- S.pure(logger.trace(s"Closing CSV reader ${r.toString}"))
        _ <- S.pure(r.close())
      } yield ()

    val reader = Resource.make(openReader)(closeReader)

    for {
      _        <- Stream.eval(S.pure(logger.debug("Reading input from CSV reader")))
      iterator <- Stream.resource(reader)
      _        <- Stream.eval(cs.shift)
      records  <- Stream.unfold(iterator)(unfoldCsv)
    } yield {
      records
    }
  }

  private[this] def unfoldCsv(iterator: KantanReader): Option[(AValidated[RawRecord], KantanReader)] = {
    def onError(e: ReadError) = {
      logger.warn(s"Reading error: ${e.toString}")
      Some((e.toString.aInvalid, iterator))
    }
    def onValid(v: RawRecord) = {
      logger.trace(s"Reading record: ${v.toString}")
      Some((v.aValid, iterator))
    }
    if (iterator.hasNext) iterator.next.fold(onError, onValid) else None
  }
}
