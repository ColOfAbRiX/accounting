package com.colofabrix.scala.accounting.etl

import cats.effect._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.utils.validation._

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
  def read: VRawInput[IO] = {
    fs2.Stream.unfold(input.iterator)(i => if (i.hasNext) Some((i.next.aValid, i)) else None)
  }
}

/**
 * Generic CSV
 */
class CsvReader[A: kantan.csv.CsvSource](input: A) extends InputReader {
  import kantan.csv._
  import kantan.csv.ops._

  private type KantanReader = kantan.csv.CsvReader[ReadResult[List[String]]]

  def read: VRawInput[IO] = {
    val openReader  = IO(input.asCsvReader[List[String]](rfc))
    val closeReader = (r: KantanReader) => IO(r.close())
    val reader      = Resource.make(openReader)(closeReader)
    for {
      iterator <- fs2.Stream.resource(reader)
      result   <- fs2.Stream.unfold(iterator)(unfoldCsv)
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
