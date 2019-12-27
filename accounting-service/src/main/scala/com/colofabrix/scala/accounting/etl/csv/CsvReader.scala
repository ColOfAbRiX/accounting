package com.colofabrix.scala.accounting.etl.csv

import java.io.File
import cats.implicits._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.utils.validation._
import cats.effect.IO
import cats.effect.Resource

/**
 * Interface for a generic CSV reader that reads raw data
 */
trait CsvReader {
  def read: VRawInput[IO]
}

/**
 * CSV Reader from Iterable
 */
class IterableCsvReader(input: Iterable[RawRecord]) extends CsvReader {
  def read: VRawInput[IO] = {
    fs2.Stream.unfold(input.iterator)(i => if (i.hasNext) Some((i.next.aValid, i)) else None)
  }
}

/**
 * CSV Reader from file
 */
class FileCsvReader(file: File) extends CsvReader {
  import kantan.csv._
  import kantan.csv.ops._

  private type KantanReader = kantan.csv.CsvReader[ReadResult[List[String]]]

  def read: VRawInput[IO] = {
    val openReader  = IO(file.asCsvReader[List[String]](rfc))
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
    def onError(e: ReadError) = Some(e.toString.aInvalid, iterator)
    def onValid(v: RawRecord) = Some(v.aValid, iterator)
    if (iterator.hasNext) iterator.next.fold(onError, onValid) else None
  }
}
