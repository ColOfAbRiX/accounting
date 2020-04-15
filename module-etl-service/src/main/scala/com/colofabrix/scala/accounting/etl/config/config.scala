package com.colofabrix.scala.accounting.etl

import cats.Show
import com.colofabrix.scala.accounting.etl.BuildInfo
import com.colofabrix.scala.accounting.etl.model.Config._
import eu.timepit.refined.pureconfig._
import pureconfig._
import pureconfig.generic.auto._
import pureconfig.generic.semiauto._

package object config {
  protected[this] val logger = org.log4s.getLogger

  /**
   * Main application configuration. It has to be loaded once per application
   * and fail-fast, as soon as a bad configuration is detected
   */
  val serviceConfig: EtlConfig =
    ConfigSource
      .default
      .at(BuildInfo.projectPackage)
      .loadOrThrow[EtlConfig]

  logger.info(s"Loaded configuration: ${Show[EtlConfig].show(serviceConfig)}")

  //  TYPECLASS INSTANCES  //

  implicit lazy val inputTypeReader: ConfigReader[InputType] = {
    deriveEnumerationReader[InputType](ConfigFieldMapping(PascalCase, PascalCase))
  }
}
