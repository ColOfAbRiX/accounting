package com.colofabrix.scala.accounting.transactions

import cats.Show
import com.colofabrix.scala.accounting.transactions.BuildInfo
import com.colofabrix.scala.accounting.transactions.model.Config._
import pureconfig._
import pureconfig.generic.auto._

package object config {
  protected[this] val logger = org.log4s.getLogger

  /**
   * Main application configuration. It has to be loaded once per application
   * and fail-fast, as soon as a bad configuration is detected
   */
  val serviceConfig: TransactionsConfig =
    ConfigSource
      .default
      .at(BuildInfo.projectPackage)
      .loadOrThrow[TransactionsConfig]

  logger.info(s"Loaded configuration: ${Show[TransactionsConfig].show(serviceConfig)}")
}
