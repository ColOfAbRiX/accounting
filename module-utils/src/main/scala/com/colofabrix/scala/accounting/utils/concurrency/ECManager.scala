package com.colofabrix.scala.accounting.utils.concurrency

import cats.effect._
import com.colofabrix.scala.accounting.utils.ADT
import java.util.concurrent._
import scala.concurrent._

sealed trait ExecutorType                          extends ADT
final case object CachedThreadPool                 extends ExecutorType
final case object SingleThreadThreadPool           extends ExecutorType
final case class FixedThreadPool(parallelism: Int) extends ExecutorType

/**
 * Creation and management of ExecutionContexts
 */
object ECManager {
  private[this] lazy val coresCount = Runtime.getRuntime.availableProcessors

  def shift[F[_]: LiftIO](ec: ExecutionContext): F[Unit] = {
    LiftIO[F].liftIO(IO.contextShift(ec).shift)
  }

  /**
   * Creates a new ExecutionContext with some predefined settings
   */
  def create(name: String, executorType: ExecutorType, priority: Int): ExecutionContext = {
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

    // See: https://blog.jessitron.com/2014/01/29/choosing-an-executorservice/
    val executor = executorType match {
      case CachedThreadPool             => Executors.newCachedThreadPool(threadFactory)
      case SingleThreadThreadPool       => Executors.newSingleThreadExecutor(threadFactory)
      case FixedThreadPool(parallelism) => Executors.newFixedThreadPool(parallelism, threadFactory)
    }

    ExecutionContext.fromExecutor(executor)
  }

  /** Creates a new CPU-bound pool, fixed to the number of CPUs dedicated to computations */
  def createCompute(name: String): ExecutionContext =
    create("%s-compute".format(name), FixedThreadPool(coresCount), Thread.NORM_PRIORITY)

  /** Creates a new blocking IO pool, unbounded and dedicated to blocking I/O operations */
  def createIo(name: String): ExecutionContext =
    create("%s-io".format(name), CachedThreadPool, Thread.NORM_PRIORITY)

  /** Creates a new non-blocking IO polling pool, high priority for I/O notifications */
  def createIoPolling(name: String): ExecutionContext =
    create("%s-iopolling".format(name), SingleThreadThreadPool, Thread.MAX_PRIORITY)
}
