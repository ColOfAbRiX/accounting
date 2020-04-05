package com.colofabrix.scala.accounting.utils.logging

import cats.effect.Sync
import fs2.Stream
import org.log4s._

/**
 * Includes a logger for fs2.Stream
 */
trait StreamLogging {
  protected def logger: Logger
  protected lazy val streamLogger: StreamLogger = new StreamLogger(new PureLogger(logger))
}

/**
 * Logger inside an fs2.Stream
 */
final protected[logging] class StreamLogger(pureLogger: PureLogger) {
  @inline def trace[F[_]: Sync](msg: String): Stream[F, Unit] = Stream.eval(pureLogger.trace(msg))
  @inline def debug[F[_]: Sync](msg: String): Stream[F, Unit] = Stream.eval(pureLogger.debug(msg))
  @inline def info[F[_]: Sync](msg: String): Stream[F, Unit]  = Stream.eval(pureLogger.info(msg))
  @inline def warn[F[_]: Sync](msg: String): Stream[F, Unit]  = Stream.eval(pureLogger.warn(msg))
  @inline def error[F[_]: Sync](msg: String): Stream[F, Unit] = Stream.eval(pureLogger.error(msg))
}
