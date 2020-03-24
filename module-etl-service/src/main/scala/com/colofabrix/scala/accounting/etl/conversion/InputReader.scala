package com.colofabrix.scala.accounting.etl.conversion

import cats.effect._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.utils._
import com.colofabrix.scala.accounting.utils.validation._
import fs2.Stream
import org.log4s._

/**
 * Interface for a generic reader that reads raw data
 */
trait InputReader {
  def read: VRawInput[IO]
}

/**
 * Reader from Iterable
 */
class IterableReader(input: Iterable[RawRecord]) extends InputReader {
  private[this] val logger = getLogger

  def read: VRawInput[IO] = {
    logger.debug("Reading input from Iterable reader")
    Stream.unfold(input.iterator)(i => if (i.hasNext) Some((i.next.aValid, i)) else None)
  }
}

/**
 * Reader of generic CSV
 */
class CsvReader[A: kantan.csv.CsvSource](input: A) extends InputReader {
  import kantan.csv._
  import kantan.csv.ops._

  private[this] type KantanReader = kantan.csv.CsvReader[ReadResult[List[String]]]
  private[this] val logger = getLogger

  def read: VRawInput[IO] = {
    val openReader = for {
      _      <- ContextShiftManager.io.shift
      reader <- IO(input.asCsvReader[List[String]](rfc))
      _      <- IO(logger.trace(s"Opened CSV reader ${reader.toString}"))
    } yield reader

    val closeReader = (r: KantanReader) =>
      for {
        _ <- ContextShiftManager.io.shift
        _ <- IO(logger.trace(s"Closing CSV reader ${r.toString}"))
        _ <- IO(r.close())
      } yield ()

    val reader = Resource.make(openReader)(closeReader)

    for {
      _        <- Stream.eval(IO(logger.debug("Reading input from CSV reader")))
      iterator <- Stream.resource(reader)
      _        <- Stream.eval(ContextShiftManager.io.shift)
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
