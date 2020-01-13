package com.colofabrix.scala.accounting.utils

import java.util.concurrent._
import java.util.concurrent.atomic.AtomicLong
import scala.concurrent._

object ExecutionContexts {

  // See: https://blog.jessitron.com/2014/01/29/choosing-an-executorservice/

  private val coresCount = Runtime.getRuntime().availableProcessors()

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

  /** Default scala global context */
  val global: ExecutionContextExecutor = ExecutionContext.global

  /** CPU-bound pool */
  val computePool: ExecutionContextExecutor = ExecutionContext.fromExecutor(
    Executors.newFixedThreadPool(coresCount, threadFactory("compute", Thread.NORM_PRIORITY)),
  )

  /** Blocking IO pool */
  val ioPool: ExecutionContextExecutor = ExecutionContext.fromExecutor(
    Executors.newCachedThreadPool(threadFactory("io", Thread.NORM_PRIORITY)),
  )

  /** Non-blocking IO polling pool */
  val eventsPool: ExecutionContextExecutor = ExecutionContext.fromExecutor(
    Executors.newFixedThreadPool(1, threadFactory("event", Thread.MAX_PRIORITY)),
  )

}
