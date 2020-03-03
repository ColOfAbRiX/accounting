package com.colofabrix.scala.accounting.transactions

import cats.Show
import com.colofabrix.scala.accounting.utils.ADT
import org.log4s._
import pureconfig._
import pureconfig.generic.auto._

object config {

  private[this] val logger = getLogger

  //  CONFIG  //

  final case class ServiceConfig(
      server: ServerConfig,
  ) extends ADT

  final case class ServerConfig(
      port: Int,
      host: String,
      debugMode: Boolean,
  ) extends ADT

  /**
   * Main application configuration
   */
  val serviceConfig: ServiceConfig =
    ConfigSource
      .default
      .at(BuildInfo.projectPackage)
      .loadOrThrow[ServiceConfig]

  logger.info(s"Loaded configuration: ${Show[ServiceConfig].show(serviceConfig)}")

}
