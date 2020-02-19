package com.colofabrix.scala.accounting.etl.model

import com.colofabrix.scala.accounting.utils.ADT

/**
 * Model used by the service configuration
 */
object Config {

  //  INPUT TYPE  //

  sealed trait InputType extends ADT {
    val description: String = this
      .getClass
      .getSimpleName
      .replaceAll("InputType.*$", "")
      .toLowerCase()
  }
  object InputType {
    def apply(value: String): InputType = value match {
      case BarclaysInputType.description => BarclaysInputType
      case HalifaxInputType.description  => HalifaxInputType
      case StarlingInputType.description => StarlingInputType
      case AmexInputType.description     => AmexInputType
    }
  }

  final case object BarclaysInputType extends InputType
  final case object HalifaxInputType  extends InputType
  final case object StarlingInputType extends InputType
  final case object AmexInputType     extends InputType

}
