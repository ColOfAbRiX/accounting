package com.colofabrix.scala.accounting.etl.model

import com.colofabrix.scala.accounting.utils.ADT
import eu.timepit.refined.types.string.NonEmptyString
import java.time.LocalDate

/**
 * Transaction in a specific Bank's format
 */
sealed trait InputTransaction extends ADT

/** Transaction on a Barclays CSV file */
final case class BarclaysTransaction(
    number: Option[Int],
    date: LocalDate,
    account: String,
    amount: BigDecimal,
    subcategory: String,
    memo: NonEmptyString,
) extends InputTransaction

/** Transaction on a American Express CSV file */
final case class AmexTransaction(
    date: LocalDate,
    reference: String,
    amount: BigDecimal,
    description: NonEmptyString,
    extra: String,
) extends InputTransaction

/** Transaction on a Halifax CSV file */
final case class HalifaxTransaction(
    date: LocalDate,
    dateEntered: LocalDate,
    reference: String,
    description: NonEmptyString,
    amount: BigDecimal,
) extends InputTransaction

/** Transaction on a Starling CSV file */
final case class StarlingTransaction(
    date: LocalDate,
    counterParty: String,
    reference: NonEmptyString,
    `type`: String,
    amount: BigDecimal,
    balance: BigDecimal,
) extends InputTransaction
