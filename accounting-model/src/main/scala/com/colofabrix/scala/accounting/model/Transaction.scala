package com.colofabrix.scala.accounting.model

import java.time.LocalDate

/**
 * Represents a transaction in the system
 */
final case class Transaction(
    date: LocalDate,
    amount: BigDecimal,
    description: String,
    input: String,
    category: String,
    subcategory: String,
    notes: String,
)
