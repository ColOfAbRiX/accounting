package com.colofabrix.scala.accounting.etl

import cats.effect._
import definitions._

/**
 * Interface for a generic reader that reads raw data
 */
trait InputReader {
  def read: VRawInput[IO]
}
