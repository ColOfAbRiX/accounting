package com.colofabrix.scala.accounting.etl.conversion

import cats.Monad
import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.utils.concurrency._
import com.colofabrix.scala.accounting.utils.logging._
import com.colofabrix.scala.accounting.utils.validation._
import fs2.Stream
import kantan.csv._
import kantan.csv.ops._

/**
 * Reader from Iterable
 */
class IterableReader[F[_]: Monad](input: Iterable[RawRecord]) extends PureLogging[F] {
  /**
   * Read from the validated raw input
   */
  def read: VRawInput[F] =
    for {
      _       <- Stream.eval(pureLogger.debug("Reading input from Iterable reader"))
      records <- Stream.unfold(input.iterator)(i => if (i.hasNext) Some((i.next.aValid, i)) else None)
    } yield records
}

/**
 * Reader of generic CSV
 */
class CsvReader[F[_]: Sync: LiftIO, A: CsvSource](input: A) extends PureLogging[F] {

  private[this] type KantanReader = kantan.csv.CsvReader[ReadResult[List[String]]]

  private[this] val openReader = for {
    _      <- ECManager.shift(DefaultEC.io)
    reader <- Sync[F].delay(input.asCsvReader[List[String]](rfc))
    _      <- pureLogger.trace(s"Opened CSV reader ${reader.toString}")
  } yield reader

  private[this] val closeReader = (r: KantanReader) =>
    for {
      _ <- ECManager.shift(DefaultEC.io)
      _ <- pureLogger.trace(s"Closing CSV reader ${r.toString}")
      _ <- Sync[F].delay(r.close())
    } yield ()

  private[this] val reader = Resource.make(openReader)(closeReader)

  /**
   * Read from the validated raw input
   */
  def read: VRawInput[F] =
    for {
      _        <- Stream.eval(pureLogger.debug("Reading input from CSV reader"))
      iterator <- Stream.resource(reader)
      _        <- Stream.eval(ECManager.shift(DefaultEC.io))
      records  <- Stream.unfold(iterator)(unfoldCsv)
    } yield records

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
