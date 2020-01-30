package com.colofabrix.scala.accounting.etl.config

import pureconfig._
import pureconfig.generic.auto._

object EtlConfig {

  val config: EtlConfig = ConfigSource
    .default
    .at("com.colofabrix.scala.accounting.etl")
    .load[EtlConfig]
    .getOrElse(EtlConfig())

  @SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
  final case class EtlConfig(
      server: ServerConfig = ServerConfig(),
  )

  @SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
  final case class ServerConfig(
      port: Int = 8080,
      host: String = "localhost",
  )

}
