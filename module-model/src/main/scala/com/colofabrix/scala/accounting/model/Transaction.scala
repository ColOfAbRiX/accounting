package com.colofabrix.scala.accounting.model

import java.time.LocalDate
import java.util.UUID

/**
 * Represents a transaction in the system
 */
trait TTransaction {
  def id: UUID
  def date: LocalDate
  def amount: BigDecimal
  def description: String
  def input: String
  def category: String
  def subcategory: String
  def notes: String
}

/**
 * A transaction
 */
final case class Transaction(
    id: UUID,
    date: LocalDate,
    amount: BigDecimal,
    description: String,
    input: String,
    category: String,
    subcategory: String,
    notes: String,
) extends TTransaction

/**
 * A transactions that is the result of multiple merged transactions
 */
final case class MergeTransaction(
    id: UUID,
    merged: List[TTransaction],
    date: LocalDate,
    amount: BigDecimal,
    description: String,
    input: String,
    category: String,
    subcategory: String,
    notes: String,
) extends TTransaction

/**
 * A transactions that is the result of amending and existing transaction
 */
final case class AmendedTransaction(
    id: UUID,
    original: TTransaction,
    date: LocalDate,
    amount: BigDecimal,
    description: String,
    input: String,
    category: String,
    subcategory: String,
    notes: String,
) extends TTransaction
