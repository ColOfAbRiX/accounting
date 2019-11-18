package com.colofabrix.scala.accounting.model

import java.time._

sealed trait BankType
final case object BarclaysBank
final case object HalifaxBank
final case object StarlingBank
final case object AmexBank
