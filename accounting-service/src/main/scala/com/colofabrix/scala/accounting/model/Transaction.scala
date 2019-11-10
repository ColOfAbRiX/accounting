package com.colofabrix.scala.accounting.model

import java.time.LocalDate

final case class Transaction(
  date: LocalDate,
  amount: BigDecimal,
  `type`: String,
  description: String,
  bank: BankType,
  category: String,
  subcategory: String,
  notes: String
)
