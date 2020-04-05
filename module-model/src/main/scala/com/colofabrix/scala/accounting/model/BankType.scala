package com.colofabrix.scala.accounting.model

import enumeratum._

/**
 * The type of Bank a transaction can have
 */
sealed abstract class BankType(override val entryName: String) extends EnumEntry

object BankType extends Enum[BankType] {
  /** Barclays transaction source */
  final case object BarclaysBank extends BankType("barclays")
  /** Halifax transaction source */
  final case object HalifaxBank extends BankType("halifax")
  /** Starling transaction source */
  final case object StarlingBank extends BankType("starling")
  /** Amex transaction source */
  final case object AmexBank extends BankType("amex")

  def apply(value: String): BankType = withName(value)

  val values = findValues
}
