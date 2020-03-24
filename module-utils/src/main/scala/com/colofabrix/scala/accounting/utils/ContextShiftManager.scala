package com.colofabrix.scala.accounting.utils

import java.util.concurrent._
import scala.concurrent._
import cats.effect.ContextShift
import cats.effect.IO

object ContextShiftManager {

  private[this] trait ExecutorType                                 extends ADT
  final private[this] case object CachedThreadPool                 extends ExecutorType
  final private[this] case class FixedThreadPool(parallelism: Int) extends ExecutorType

  // See: https://blog.jessitron.com/2014/01/29/choosing-an-executorservice/

  private[this] lazy val coresCount = Runtime.getRuntime.availableProcessors

  private[this] def newContextShift(name: String, executorType: ExecutorType, priority: Int): ContextShift[IO] = {
    val threadFactory: ThreadFactory = new ThreadFactory {
      val threadCounter = new java.util.concurrent.atomic.AtomicInteger(0)

      val group = Option(System.getSecurityManager)
        .map(_.getThreadGroup)
        .getOrElse(Thread.currentThread.getThreadGroup)

      def newThread(runnable: Runnable): Thread = {
        val threadName = "%s-thread-%d".format(name, threadCounter.getAndIncrement)
        val thread     = new Thread(group, runnable, threadName)
        thread.setPriority(priority)
        thread
      }
    }

    val executor = executorType match {
      case CachedThreadPool             => Executors.newCachedThreadPool(threadFactory)
      case FixedThreadPool(parallelism) => Executors.newFixedThreadPool(parallelism, threadFactory)
    }

    IO.contextShift(ExecutionContext.fromExecutor(executor))
  }

  // See https://gist.github.com/djspiewak/46b543800958cf61af6efa8e072bfd5c
  // See https://typelevel.org/cats-effect/concurrency/basics.html

  /** Default scala global context */
  lazy val global: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  /** CPU-bound pool, fixed to the number of CPUs dedicated to computations */
  lazy val compute: ContextShift[IO] = newContextShift("compute", FixedThreadPool(coresCount), Thread.NORM_PRIORITY)

  /** Blocking IO pool, unbounded and dedicated to blocking I/O operations */
  lazy val io: ContextShift[IO] = newContextShift("io", CachedThreadPool, Thread.NORM_PRIORITY)

  /** Non-blocking IO polling pool, high priority for I/O notifications */
  lazy val events: ContextShift[IO] = newContextShift("event", CachedThreadPool, Thread.MAX_PRIORITY)

}
