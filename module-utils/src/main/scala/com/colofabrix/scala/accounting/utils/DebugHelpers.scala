package com.colofabrix.scala.accounting.utils

import cats.effect._

/**
 * Helpers to test streams and validated streams
 */
trait StreamHelpers {
  implicit class StreamIOTest[A](self: fs2.Stream[IO, A]) {
    def compiled: List[A] = self.compile.toList.unsafeRunSync
  }
}
