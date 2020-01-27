package com.colofabrix.scala.accounting.model

import java.time.LocalDate

sealed trait InputType
object InputType {
  val all: List[String] = List("barclays", "halifax", "starling", "amex")
}
final case object BarclaysInputType extends InputType
final case object HalifaxInputType  extends InputType
final case object StarlingInputType extends InputType
final case object AmexInputType     extends InputType

/**
 * Transaction in a specific Bank's format
 */
sealed trait InputTransaction

/** Transaction on a Barclays CSV file */
final case class BarclaysTransaction(
    number: Option[Int],
    date: LocalDate,
    account: String,
    amount: BigDecimal,
    subcategory: String,
    memo: String,
) extends InputTransaction

/** Transaction on a American Express CSV file */
final case class AmexTransaction(
    date: LocalDate,
    reference: String,
    amount: BigDecimal,
    description: String,
    extra: String,
) extends InputTransaction

/** Transaction on a Halifax CSV file */
final case class HalifaxTransaction(
    date: LocalDate,
    dateEntered: LocalDate,
    reference: String,
    description: String,
    amount: BigDecimal,
) extends InputTransaction

/** Transaction on a Starling CSV file */
final case class StarlingTransaction(
    date: LocalDate,
    counterParty: String,
    reference: String,
    `type`: String,
    amount: BigDecimal,
    balance: BigDecimal,
) extends InputTransaction
