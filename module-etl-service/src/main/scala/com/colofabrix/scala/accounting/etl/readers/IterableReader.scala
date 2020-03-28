package com.colofabrix.scala.accounting.etl.readers

import cats.effect._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.utils.logging._
import com.colofabrix.scala.accounting.utils.validation._
import fs2.Stream

/**
 * Reader from Iterable
 */
class IterableReader[F[_]: Sync](input: Iterable[RawRecord]) extends StreamLogging {
  protected[this] val logger = org.log4s.getLogger

  /**
   * Read from the validated raw input
   */
  def read: VRawInput[F] =
    for {
      _       <- streamLogger.debug[F]("Reading input from Iterable reader")
      records <- Stream.unfold(input.iterator)(i => if (i.hasNext) Some((i.next.aValid, i)) else None)
    } yield records

}
