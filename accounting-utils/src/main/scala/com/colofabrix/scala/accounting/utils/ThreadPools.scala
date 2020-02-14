package com.colofabrix.scala.accounting.utils

import java.util.concurrent._
import java.util.concurrent.atomic.AtomicLong
import scala.concurrent._
import cats.effect.ContextShift
import cats.effect.IO

object ThreadPools {

  // See: https://blog.jessitron.com/2014/01/29/choosing-an-executorservice/

  private lazy val coresCount = Runtime.getRuntime().availableProcessors()

  private def threadFactory(name: String, priority: Int) = new ThreadFactory {
    private val counter = new AtomicLong(0L)
    def newThread(r: Runnable) = {
      val th = new Thread(r)
      th.setName(name + "-thread-" + counter.getAndIncrement.toString)
      th.setPriority(priority)
      th.setDaemon(true)
      th
    }
  }

  // See https://gist.github.com/djspiewak/46b543800958cf61af6efa8e072bfd5c
  // See https://typelevel.org/cats-effect/concurrency/basics.html

  /**
   * Default scala global context
   * */
  lazy val global: ExecutionContextExecutor = ExecutionContext.global

  /**
   * CPU-bound pool, fixed to the number of CPUs dedicated to computations
   */
  lazy val computeCs: ContextShift[IO] = IO.contextShift(compute)
  lazy val compute: ExecutionContextExecutor = ExecutionContext.fromExecutor(
    Executors.newFixedThreadPool(coresCount, threadFactory("compute", Thread.NORM_PRIORITY)),
  )

  /**
   * Blocking IO pool, unbounded and dedicated to blocking I/O operations
   */
  lazy val ioCs: ContextShift[IO] = IO.contextShift(io)
  lazy val io: ExecutionContextExecutor = ExecutionContext.fromExecutor(
    Executors.newCachedThreadPool(threadFactory("io", Thread.NORM_PRIORITY)),
  )

  /**
   * Non-blocking IO polling pool, high priority for I/O notifications
   */
  lazy val eventsCs: ContextShift[IO] = IO.contextShift(events)
  lazy val events: ExecutionContextExecutor = ExecutionContext.fromExecutor(
    Executors.newFixedThreadPool(1, threadFactory("event", Thread.MAX_PRIORITY)),
  )

}
