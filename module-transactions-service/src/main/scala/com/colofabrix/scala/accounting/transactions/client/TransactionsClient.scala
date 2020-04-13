package com.colofabrix.scala.accounting.transactions.client

import cats.effect._
import com.colofabrix.scala.accounting.utils.logging._

/**
 * Transactions Client interface
 */
trait TransactionsClient[F[_]] {
  def test: F[String]
}

/**
 * Transactions Client standard implementation
 */
final class TransactionsClientImpl extends TransactionsClient[IO] with PureLogging {
  protected[this] val logger = org.log4s.getLogger

  def test: IO[String] = IO("test")
}
