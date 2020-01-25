package com.colofabrix.scala.accounting.model

import java.time.LocalDate

@SuppressWarnings(Array("org.wartremover.warts.All"))
object newtypes {
  // Newtypes for Transaction
  final case class TrAmount(value: BigDecimal)
  final case class TrCategory(value: String)
  final case class TrDate(value: LocalDate)
  final case class TrDescription(value: String)
  final case class TrInput(value: String)
  final case class TrNotes(value: String)
  final case class TrSubcategory(value: String)

  // Newtypes for TransactionInput
  final case class InAccount(value: String)
  final case class InAmount(value: BigDecimal)
  final case class InBalance(value: BigDecimal)
  final case class InCounterParty(value: String)
  final case class InDate(value: LocalDate)
  final case class InDateEntered(value: LocalDate)
  final case class InDescription(value: String)
  final case class InExtra(value: String)
  final case class InMemo(value: String)
  final case class InNumber(value: Option[Int])
  final case class InReference(value: String)
  final case class InSubcategory(value: String)
  final case class InType(value: String)
}
