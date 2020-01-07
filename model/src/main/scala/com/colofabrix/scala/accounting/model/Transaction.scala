package com.colofabrix.scala.accounting.model

import java.time.LocalDate

/** Name of the input */
final case class InputName(name: String)

/**
 * Represents a transaction in the system
 */
final case class Transaction(
    date: LocalDate,
    amount: BigDecimal,
    description: String,
    bank: InputName,
    category: String,
    subcategory: String,
    notes: String,
)
