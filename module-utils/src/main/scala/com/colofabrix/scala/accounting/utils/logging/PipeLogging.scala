package com.colofabrix.scala.accounting.utils.logging

import fs2.Pipe

/**
 * Helpers for logging of pipes
 */
object PipeLogging {
  private[this] val logger = org.log4s.getLogger

  implicit class PipeLoggingImpl[F[_], I, O](pipe: Pipe[F, I, O]) {
    /** Logs entries passings at the input of the pipeline at error level */
    def errorI(f: I => String): Pipe[F, I, O] = PipeLogging.error(f) andThen pipe
    /** Logs entries passings at the input of the pipeline at warn level */
    def warnI(f: I => String): Pipe[F, I, O] = PipeLogging.warn(f) andThen pipe
    /** Logs entries passings at the input of the pipeline at info level */
    def infoI(f: I => String): Pipe[F, I, O] = PipeLogging.info(f) andThen pipe
    /** Logs entries passings at the input of the pipeline at debug level */
    def debugI(f: I => String): Pipe[F, I, O] = PipeLogging.debug(f) andThen pipe
    /** Logs entries passings at the input of the pipeline at trace level */
    def traceI(f: I => String): Pipe[F, I, O] = PipeLogging.trace(f) andThen pipe

    /** Logs entries passings at the output of the pipeline at error level */
    def errorO(f: O => String): Pipe[F, I, O] = pipe andThen PipeLogging.error(f)
    /** Logs entries passings at the output of the pipeline at warn level */
    def warnO(f: O => String): Pipe[F, I, O] = pipe andThen PipeLogging.warn(f)
    /** Logs entries passings at the output of the pipeline at info level */
    def infoO(f: O => String): Pipe[F, I, O] = pipe andThen PipeLogging.info(f)
    /** Logs entries passings at the output of the pipeline at debug level */
    def debugO(f: O => String): Pipe[F, I, O] = pipe andThen PipeLogging.debug(f)
    /** Logs entries passings at the output of the pipeline at trace level */
    def traceO(f: O => String): Pipe[F, I, O] = pipe andThen PipeLogging.trace(f)
  }

  /** Creates a pipe that logs a message at error level */
  def error[F[_], A](f: A => String): Pipe[F, A, A] = _.debug(f, logger.error(_))
  /** Creates a pipe that logs a message at warn level */
  def warn[F[_], A](f: A => String): Pipe[F, A, A] = _.debug(f, logger.warn(_))
  /** Creates a pipe that logs a message at info level */
  def info[F[_], A](f: A => String): Pipe[F, A, A] = _.debug(f, logger.info(_))
  /** Creates a pipe that logs a message at debug level */
  def debug[F[_], A](f: A => String): Pipe[F, A, A] = _.debug(f, logger.debug(_))
  /** Creates a pipe that logs a message at trace level */
  def trace[F[_], A](f: A => String): Pipe[F, A, A] = _.debug(f, logger.trace(_))
}
