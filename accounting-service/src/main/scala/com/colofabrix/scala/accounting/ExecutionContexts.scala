package com.colofabrix.scala.accounting

import java.util.concurrent._
import java.util.concurrent.atomic.AtomicLong
import scala.concurrent.ExecutionContext

object ExecutionContexts {

  // See: https://blog.jessitron.com/2014/01/29/choosing-an-executorservice/

  private val coresCount = Runtime.getRuntime().availableProcessors()

  private def threadFactory(name: String, priority: Int = Thread.NORM_PRIORITY) = new ThreadFactory {
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
  val global = ExecutionContext.global
  /** CPU-bound pool */
  val computePool = ExecutionContext.fromExecutor(
    Executors.newFixedThreadPool(coresCount, threadFactory("compute")),
  )
  /** Blocking IO pool */
  val ioPool = ExecutionContext.fromExecutor(
    Executors.newCachedThreadPool(threadFactory("io")),
  )
  /** Non-blocking IO polling pool */
  val eventsPool = ExecutionContext.fromExecutor(
    Executors.newFixedThreadPool(1, threadFactory("event", Thread.MAX_PRIORITY)),
  )

}
