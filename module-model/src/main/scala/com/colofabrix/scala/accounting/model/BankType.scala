package com.colofabrix.scala.accounting.model

import enumeratum._

sealed abstract class BankType(override val entryName: String) extends EnumEntry

object BankType extends Enum[BankType] {
  final case object BarclaysBank extends BankType("barclays")
  final case object HalifaxBank  extends BankType("halifax")
  final case object StarlingBank extends BankType("starling")
  final case object AmexBank     extends BankType("amex")

  def apply(value: String): BankType = withName(value)

  val values = findValues
}
