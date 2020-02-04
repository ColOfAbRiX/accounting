package com.colofabrix.scala.accounting.etl.model

import com.colofabrix.scala.accounting.utils.ADT

/**
 * Model used by the service configuration
 */
object Config {

  //  CONFIG  //

  final case class EtlConfig(
      server: ServerConfig,
      inputTypes: Set[InputType],
  ) extends ADT

  final case class ServerConfig(
      port: Int,
      host: String,
  ) extends ADT

  //  INPUT TYPE  //

  sealed trait InputType extends ADT {
    val description: String = this
      .getClass()
      .getSimpleName()
      .replaceAll("InputType.*$", "")
      .toLowerCase()
  }
  final case object BarclaysInputType extends InputType
  final case object HalifaxInputType  extends InputType
  final case object StarlingInputType extends InputType
  final case object AmexInputType     extends InputType

}
