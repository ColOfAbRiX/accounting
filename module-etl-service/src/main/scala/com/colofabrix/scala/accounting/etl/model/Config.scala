package com.colofabrix.scala.accounting.etl.model

import com.colofabrix.scala.accounting.utils.ADT
import enumeratum._

import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric._
import eu.timepit.refined.string.IPv4

/**
 * Model used by the service configuration
 */
object Config {

  /**
   * Type of input of a transaction
   */
  sealed abstract class InputType(override val entryName: String) extends EnumEntry

  object InputType extends Enum[InputType] {
    /** Input of type Barclays */
    final case object BarclaysInputType extends InputType("barclays")
    /** Input of type Halifax */
    final case object HalifaxInputType extends InputType("halifax")
    /** Input of type Starling */
    final case object StarlingInputType extends InputType("starling")
    /** Input of type Amex */
    final case object AmexInputType extends InputType("amex")

    def apply(value: String): InputType = withName(value)

    val values = findValues
  }

  /**
   * Configuration of the ETL service
   */
  final case class EtlConfig(
      server: ServerConfig,
      inputTypes: Set[InputType],
  ) extends ADT

  /**
   * Configuration of the HTTP server
   */
  final case class ServerConfig(
      port: Int Refined Interval.Closed[W.`1024`.T, W.`65535`.T],
      host: String Refined IPv4,
      debugMode: Boolean,
  ) extends ADT

}
