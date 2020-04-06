package com.colofabrix.scala.accounting.utils.logging

import fs2.Pipe
import org.log4s._

/**
 * Includes a logger for fs2.Pipe
 */
trait PipeLogging {
  protected def logger: Logger
  protected lazy val pipeLogger: PipeLogger = new PipeLogger(logger)
}

/**
 * Logger inside an fs2.Pipe
 */
final protected[logging] class PipeLogger(logger: Logger) {
  @inline def trace[F[_], A](show: A => String): Pipe[F, A, A] = _.debug(show, logger.trace(_))
  @inline def trace[F[_], A](msg: String): Pipe[F, A, A]       = trace(_ => msg)

  @inline def debug[F[_], A](show: A => String): Pipe[F, A, A] = _.debug(show, logger.debug(_))
  @inline def debug[F[_], A](msg: String): Pipe[F, A, A]       = debug(_ => msg)

  @inline def info[F[_], A](show: A => String): Pipe[F, A, A] = _.debug(show, logger.info(_))
  @inline def info[F[_], A](msg: String): Pipe[F, A, A]       = info(_ => msg)

  @inline def warn[F[_], A](show: A => String): Pipe[F, A, A] = _.debug(show, logger.warn(_))
  @inline def warn[F[_], A](msg: String): Pipe[F, A, A]       = warn(_ => msg)

  @inline def error[F[_], A](show: A => String): Pipe[F, A, A] = _.debug(show, logger.error(_))
  @inline def error[F[_], A](msg: String): Pipe[F, A, A]       = error(_ => msg)

  @inline def throwable[F[_], A](t: Throwable, show: A => String): Pipe[F, A, A] = _.debug(show, logger.error(t)(_))
  @inline def throwable[F[_], A](t: Throwable, msg: String): Pipe[F, A, A]       = throwable(t, _ => msg)

}
