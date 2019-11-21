package com.colofabrix.scala.accounting.model

import java.time._

/**
  * Transaction in a specific Bank's format
  */
trait BankTransaction

//final case class HalifaxRow(
//  date: LocalDate,
//  dateEntered: LocalDate,
//  reference: String,
//  description: String,
//  amount: BigDecimal
//) extends BankTransaction
//
//final case class StarlingRow(
//  date: LocalDate,
//  counterParty: String,
//  reference: String,
//  `type`: String,
//  amount: BigDecimal,
//  balance: BigDecimal
//) extends BankTransaction
//
//final case class AmexRow(
//  date: LocalDate,
//  reference: String,
//  amount: BigDecimal,
//  description: String,
//  extra: String
//) extends BankTransaction
