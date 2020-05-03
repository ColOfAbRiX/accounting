package com.colofabrix.scala.accounting.utils

import cats.effect._
import org.scalactic.Prettifier

/**
 * Helpers to test streams and validated streams
 */
trait StreamHelpers {
  implicit class StreamIOTest[A](self: fs2.Stream[IO, A]) {
    def compiled: List[A] = self.compile.toList.unsafeRunSync
  }
}

trait PPrintPrettifier {
  implicit val prettifier: Prettifier = new Prettifier {
    override def apply(o: Any): String = pprint.apply(o).toString() + "\n"
  }
}
