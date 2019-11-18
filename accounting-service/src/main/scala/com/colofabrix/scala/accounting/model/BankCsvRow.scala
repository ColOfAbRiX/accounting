package com.colofabrix.scala.accounting.model

import java.time._

sealed trait BankTransaction

final case class BarclaysRow(
  number: Option[Int],
  date: LocalDate,
  account: String,
  amount: BigDecimal,
  subcategory: String,
  memo: String
) extends BankTransaction

final case class HalifaxRow(
  date: LocalDate,
  dateEntered: LocalDate,
  reference: String,
  description: String,
  amount: BigDecimal
) extends BankTransaction

final case class StarlingRow(
  date: LocalDate,
  counterParty: String,
  reference: String,
  `type`: String,
  amount: BigDecimal,
  balance: BigDecimal
) extends BankTransaction

final case class AmexRow(
  date: LocalDate,
  reference: String,
  amount: BigDecimal,
  description: String,
  extra: String
) extends BankTransaction
