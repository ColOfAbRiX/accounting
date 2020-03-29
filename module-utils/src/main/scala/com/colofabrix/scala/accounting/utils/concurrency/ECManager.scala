package com.colofabrix.scala.accounting.utils.concurrency

import cats.effect._
import com.colofabrix.scala.accounting.utils.ADT
import java.util.concurrent._
import scala.concurrent._

sealed trait ExecutorType                            extends ADT
final case object CachedThreadsExecutor              extends ExecutorType
final case object SingleThreadExecutor               extends ExecutorType
final case class FixedCountExecutor(n: Int)          extends ExecutorType
final case class CustomExecutor(es: ExecutorService) extends ExecutorType

/**
 * Creation and management of ExecutionContexts
 */
object ECManager {
  private[this] lazy val coresCount = Runtime.getRuntime.availableProcessors

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
        val count      = threadCounter.getAndIncrement.toString
        val threadName = if (name.isEmpty) s"thread-$count" else s"$name-thread-$count"
        val thread     = new Thread(group, runnable, threadName)
        thread.setPriority(priority)
        thread
      }
    }

    // See: https://blog.jessitron.com/2014/01/29/choosing-an-executorservice/
    val executor = executorType match {
      case CachedThreadsExecutor           => Executors.newCachedThreadPool(threadFactory)
      case SingleThreadExecutor            => Executors.newSingleThreadExecutor(threadFactory)
      case FixedCountExecutor(parallelism) => Executors.newFixedThreadPool(parallelism, threadFactory)
      case CustomExecutor(executor)        => executor
    }

    ExecutionContext.fromExecutor(executor)
  }

  /** Explicitly shift an computation inside an effect F[_] given a LiftIO[F] */
  def shift[F[_]: LiftIO](ec: ExecutionContext): F[Unit] = {
    LiftIO[F].liftIO(IO.contextShift(ec).shift)
  }

  /** Creates a new CPU-bound pool, fixed to the number of CPUs dedicated to computations */
  def createCompute(prefix: String): ExecutionContext = {
    val fullPrefix = if (prefix.isEmpty) s"compute" else s"$prefix-compute"
    create(fullPrefix, FixedCountExecutor(coresCount), Thread.NORM_PRIORITY)
  }

  /** Creates a new blocking IO pool, unbounded and dedicated to blocking I/O operations */
  def createIo(prefix: String): ExecutionContext = {
    val fullPrefix = if (prefix.isEmpty) s"io" else s"$prefix-io"
    create(fullPrefix, CachedThreadsExecutor, Thread.NORM_PRIORITY)
  }

  /** Creates a new non-blocking IO polling pool, high priority for I/O notifications */
  def createEvents(prefix: String): ExecutionContext = {
    val fullPrefix = if (prefix.isEmpty) s"events" else s"$prefix-events"
    create(fullPrefix, SingleThreadExecutor, Thread.MAX_PRIORITY)
  }
}
