package com.colofabrix.scala.accounting.utils.logging

import cats.Monad
import org.log4s._

/**
 * Trait that includes a logger
 */
trait Logging {
  protected val logger = org.log4s.getLogger
}

/**
 * Trait that includes a Logger and a PureLogger
 */
abstract class PureLogging[F[_]: Monad] {
  protected val logger     = org.log4s.getLogger
  protected val pureLogger = new PureLogger[F](logger)
}

/**
 * Wrapper for pure logging
 */
final class PureLogger[F[_]: Monad](val logger: Logger) {
  @inline def trace(msg: String): F[Unit] = Monad[F].pure(logger.trace(msg))
  @inline def debug(msg: String): F[Unit] = Monad[F].pure(logger.debug(msg))
  @inline def info(msg: String): F[Unit]  = Monad[F].pure(logger.info(msg))
  @inline def warn(msg: String): F[Unit]  = Monad[F].pure(logger.warn(msg))
  @inline def error(msg: String): F[Unit] = Monad[F].pure(logger.error(msg))
}
