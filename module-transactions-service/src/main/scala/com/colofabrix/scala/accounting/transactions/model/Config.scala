package com.colofabrix.scala.accounting.transactions.model

import com.colofabrix.scala.accounting.utils.ADT

/**
 * Model used by the service configuration
 */
object Config {

  /**
   * Configuration of the Transactions service
   */
  final case class TransactionsConfig(
      server: ServerConfig,
  ) extends ADT

  /**
   * Configuration of the HTTP server
   */
  final case class ServerConfig(
      port: Int,
      host: String,
      debugMode: Boolean,
  ) extends ADT

}
