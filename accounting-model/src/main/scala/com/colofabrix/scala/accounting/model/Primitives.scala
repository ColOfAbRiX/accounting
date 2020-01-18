package com.colofabrix.scala.accounting.model

import java.time.LocalDate

final case class TrDate(value: LocalDate)
final case class TrAmount(value: BigDecimal)
final case class TrDescription(value: String)
final case class TrInput(value: String)
final case class TrCategory(value: String)
final case class TrSubcategory(value: String)
final case class TrNotes(value: String)
