package com.colofabrix.scala.accounting.etl.model

import enumeratum._

/**
 * Model used by the service configuration
 */
object Config {

  //  INPUT TYPE  //

  sealed abstract class InputType(override val entryName: String) extends EnumEntry

  object InputType extends Enum[InputType] {
    final case object BarclaysInputType extends InputType("barclays")
    final case object HalifaxInputType  extends InputType("halifax")
    final case object StarlingInputType extends InputType("starling")
    final case object AmexInputType     extends InputType("amex")

    def apply(value: String): InputType = withName(value)

    val values = findValues
  }

}
