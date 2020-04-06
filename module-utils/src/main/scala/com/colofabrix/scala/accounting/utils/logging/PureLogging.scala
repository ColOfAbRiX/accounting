package com.colofabrix.scala.accounting.utils.logging

import cats.effect.Sync
import org.log4s._

/**
 * Includes an pure logger
 */
trait PureLogging {
  protected def logger: Logger
  protected lazy val pureLogger: PureLogger = new PureLogger(logger)
}

/**
 * Logger inside an effect F[_]
 */
final protected[logging] class PureLogger(logger: Logger) {
  @inline def trace[F[_]: Sync](msg: String): F[Unit]                   = Sync[F].delay(logger.trace(msg))
  @inline def debug[F[_]: Sync](msg: String): F[Unit]                   = Sync[F].delay(logger.debug(msg))
  @inline def info[F[_]: Sync](msg: String): F[Unit]                    = Sync[F].delay(logger.info(msg))
  @inline def warn[F[_]: Sync](msg: String): F[Unit]                    = Sync[F].delay(logger.warn(msg))
  @inline def error[F[_]: Sync](msg: String): F[Unit]                   = Sync[F].delay(logger.error(msg))
  @inline def throwable[F[_]: Sync](t: Throwable, msg: String): F[Unit] = Sync[F].delay(logger.error(t)(msg))

}
