package com.colofabrix.scala.accounting.utils.concurrency

import scala.concurrent._

/**
 * Default global ExecutionContexts
 */
object DefaultEC {
  // See https://gist.github.com/djspiewak/46b543800958cf61af6efa8e072bfd5c

  /** Default scala global context */
  lazy val global: ExecutionContext = ExecutionContext.global

  /** Default CPU-bound pool, fixed to the number of CPUs dedicated to computations */
  lazy val compute: ExecutionContext = ECManager.createCompute("default")

  /** Default blocking IO pool, unbounded and dedicated to blocking I/O operations */
  lazy val io: ExecutionContext = ECManager.createIo("default")

  /** Default non-blocking IO polling pool, high priority for I/O notifications */
  lazy val events: ExecutionContext = ECManager.createEvents("default")
}
