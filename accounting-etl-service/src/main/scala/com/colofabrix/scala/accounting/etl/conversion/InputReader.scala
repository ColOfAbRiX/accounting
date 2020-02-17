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
 * Generic CSV
 */
class CsvReader[A: kantan.csv.CsvSource](input: A) extends InputReader {
  import kantan.csv._
  import kantan.csv.ops._

  private[this] type KantanReader = kantan.csv.CsvReader[ReadResult[List[String]]]
  private[this] val logger = getLogger

  def read: VRawInput[IO] = {
    val openReader  = IO(input.asCsvReader[List[String]](rfc))
    val closeReader = (r: KantanReader) => IO(r.close())
    val reader      = Resource.make(openReader)(closeReader)
    for {
      _        <- Stream.eval(ThreadPools.ioCs.shift)
      _        <- Stream.eval(IO(logger.debug("Reading input from CSV reader")))
      iterator <- Stream.resource(reader)
      _        <- Stream.eval(ThreadPools.computeCs.shift)
      result   <- Stream.unfold(iterator)(unfoldCsv)
    } yield {
      result
    }
  }

  private def unfoldCsv(iterator: KantanReader): Option[(AValidated[RawRecord], KantanReader)] = {
    def onError(e: ReadError) = Some((e.toString.aInvalid, iterator))
    def onValid(v: RawRecord) = Some((v.aValid, iterator))
    if (iterator.hasNext) iterator.next.fold(onError, onValid) else None
  }
}
