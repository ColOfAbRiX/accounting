package com.colofabrix.scala.accounting.etl.readers

import cats.effect._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.utils.logging._
import com.colofabrix.scala.accounting.utils.validation._
import fs2.Stream
import kantan.csv._
import kantan.csv.ops._

/**
 * Reader of generic CSV
 */
final class CsvReader[F[_]: Sync, A: CsvSource](input: A) extends StreamLogging {
  protected[this] val logger = org.log4s.getLogger

  private[this] type KantanReader = kantan.csv.CsvReader[ReadResult[List[String]]]

  private[this] val openReader = Sync[F].delay {
    val reader = input.asCsvReader[List[String]](rfc)
    logger.trace(s"Opened CSV reader ${reader.toString}")
    reader
  }

  private[this] val closeReader = (r: KantanReader) =>
    Sync[F].delay {
      logger.trace(s"Closing CSV reader ${r.toString}")
      r.close()
    }

  private[this] val reader = Resource.make(openReader)(closeReader)

  /**
   * Read from the validated raw input
   */
  def read: VRawInput[F] =
    for {
      _        <- streamLogger.debug[F]("Using CSV reader")
      iterator <- Stream.resource(reader)
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
