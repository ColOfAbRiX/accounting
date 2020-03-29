package com.colofabrix.scala.accounting.model

import java.time.LocalDate
import java.util.UUID

/**
 * Represents a transaction in the system
 */
trait Transaction {
  def id: UUID
  def date: LocalDate
  def amount: BigDecimal
  def description: String
  def input: BankType
  def category: String
  def subcategory: String
  def notes: String
}

/**
 * A single transaction
 */
final case class SingleTransaction(
    id: UUID,
    date: LocalDate,
    amount: BigDecimal,
    description: String,
    input: BankType,
    category: String,
    subcategory: String,
    notes: String,
) extends Transaction

/**
 * A transaction that is the result of multiple merged transactions
 */
final case class MergeTransaction(
    id: UUID,
    date: LocalDate,
    amount: BigDecimal,
    description: String,
    input: BankType,
    category: String,
    subcategory: String,
    notes: String,
    merged: List[Transaction],
) extends Transaction

/**
 * A transactions that is the result of amending an existing transaction
 */
final case class AmendedTransaction(
    id: UUID,
    date: LocalDate,
    amount: BigDecimal,
    description: String,
    input: BankType,
    category: String,
    subcategory: String,
    notes: String,
    original: Transaction,
) extends Transaction
