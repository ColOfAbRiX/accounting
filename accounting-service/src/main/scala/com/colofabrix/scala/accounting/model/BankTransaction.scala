package com.colofabrix.scala.accounting.model

import java.time.LocalDate


/**
  * Transaction in a specific Bank's format
  */
sealed trait BankTransaction

/** Transaction on a Barclays CSV file */
final case class BarclaysTransaction(
    number: Option[Int],
    date: LocalDate,
    account: String,
    amount: BigDecimal,
    subcategory: String,
    memo: String
) extends BankTransaction

/** Transaction on a American Express CSV file */
final case class AmexTransaction(
    date: LocalDate,
    reference: String,
    amount: BigDecimal,
    description: String,
    extra: String
) extends BankTransaction

/** Transaction on a Halifax CSV file */
final case class HalifaxTransaction(
    date: LocalDate,
    dateEntered: LocalDate,
    reference: String,
    description: String,
    amount: BigDecimal
) extends BankTransaction

/** Transaction on a Starling CSV file */
final case class StarlingTransaction(
    date: LocalDate,
    counterParty: String,
    reference: String,
    `type`: String,
    amount: BigDecimal,
    balance: BigDecimal
) extends BankTransaction
