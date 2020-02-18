package com.colofabrix.scala.accounting.etl

import cats.Show
import com.colofabrix.scala.accounting.etl.model.Config._
import com.colofabrix.scala.accounting.utils.ADT
import com.colofabrix.scala.accounting.BuildInfo
import org.log4s._
import pureconfig._
import pureconfig.generic.auto._
import pureconfig.generic.semiauto._

object config {

  private[this] val logger = getLogger

  //  CONFIG  //

  final case class EtlConfig(
      server: ServerConfig,
      inputTypes: Set[InputType],
  ) extends ADT

  final case class ServerConfig(
      port: Int,
      host: String,
      debugMode: Boolean,
  ) extends ADT

  /**
   * Main application configuration
   */
  val serviceConfig: EtlConfig =
    ConfigSource
      .default
      .at(BuildInfo.projectPackage)
      .loadOrThrow[EtlConfig]

  logger.info(s"Loaded configuration: ${Show[EtlConfig].show(serviceConfig)}")

  //  TYPECLASS INSTANCES  //

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  implicit lazy val inputTypeReader: ConfigReader[InputType] = {
    deriveEnumerationReader[InputType](ConfigFieldMapping(PascalCase, PascalCase))
  }

}
