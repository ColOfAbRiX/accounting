package com.colofabrix.scala.accounting.model

import java.time.LocalDate

/**
 * Represents a transaction in the system
 */
final case class Transaction(
    date: TrDate,
    amount: TrAmount,
    description: TrDescription,
    input: TrInput,
    category: TrCategory,
    subcategory: TrSubcategory,
    notes: TrNotes,
)
object Transaction {
  def apply(
      date: LocalDate,
      amount: BigDecimal,
      description: String,
      input: String,
      category: String,
      subcategory: String,
      notes: String,
  ): Transaction = Transaction(
    TrDate(date),
    TrAmount(amount),
    TrDescription(description),
    TrInput(input),
    TrCategory(category),
    TrSubcategory(subcategory),
    TrNotes(notes),
  )
}
