package com.colofabrix.scala.accounting.etl

import cats.Show
import com.colofabrix.scala.accounting.etl.model.Config._
import org.log4s._
import pureconfig._
import pureconfig.generic.auto._
import pureconfig.generic.semiauto._

object config {

  private[this] val logger = getLogger

  //  TYPECLASS INSTANCES  //

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  implicit val inputTypeReader: ConfigReader[InputType] = {
    deriveEnumerationReader[InputType](ConfigFieldMapping(PascalCase, PascalCase))
  }

  //  CONFIG  //

  // The configuration has to fail on startup if something is wrong
  val etlConfig: EtlConfig =
    ConfigSource
      .default
      .at("com.colofabrix.scala.accounting.etl")
      .loadOrThrow[EtlConfig]

  logger.info(s"Loaded configuration: ${Show[EtlConfig].show(etlConfig)}")

}
