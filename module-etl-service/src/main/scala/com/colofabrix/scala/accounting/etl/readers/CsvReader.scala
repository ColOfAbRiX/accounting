package com.colofabrix.scala.accounting.etl.readers

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.utils.logging._
import com.colofabrix.scala.accounting.utils.validation._
import fs2.Stream
import kantan.csv._
import kantan.csv.ops._

/**
 * Reader of generic CSV
 */
final class CsvReader[F[_]: Sync: ContextShift, A: CsvSource] private (input: A)
    extends PureLogging
    with StreamLogging {

  protected[this] val logger = org.log4s.getLogger

  private[this] type KantanReader = kantan.csv.CsvReader[ReadResult[List[String]]]

  private[this] val openReader =
    for {
      reader <- Sync[F].delay(input.asCsvReader[List[String]](rfc))
      _      <- pureLogger.trace(s"Opened CSV reader ${reader.toString}")
    } yield reader

  private[this] val closeReader = (r: KantanReader) =>
    for {
      _ <- pureLogger.trace(s"Closing CSV reader ${r.toString}")
      _ <- Sync[F].delay(r.close())
    } yield ()

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

  /**
   * Read from the validated raw input
   */
  def read: VRawInput[F] =
    for {
      _        <- streamLogger.debug[F]("Using CSV reader")
      iterator <- Stream.resource(Resource.make(openReader)(closeReader))
      records  <- Stream.unfold(iterator)(unfoldCsv)
      _        <- Stream.eval(ContextShift[F].shift)
    } yield records

}

object CsvReader {
  def apply[F[_]: Sync: ContextShift, A: CsvSource](input: A): CsvReader[F, A] = new CsvReader[F, A](input)
}
