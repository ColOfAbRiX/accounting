package com.colofabrix.scala.accounting.etl

import fs2.Pure
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._
import cats.effect.IO

object definitions {

  /** A raw record that comes from the input */
  type RawRecord = List[String]

  /** An input as collection of RawRecords */
  type RawInput = fs2.Stream[Pure, RawRecord]

  /** A bank input as collection of transactions */
  type BankInputs[+T <: InputTransaction] = fs2.Stream[Pure, T]

  /** A bank input as collection of validated data */
  type BankInputsV[+T <: InputTransaction] = fs2.Stream[Pure, AValidated[T]]

  type StreamIO[A] = IO[fs2.Stream[Pure, AValidated[A]]]

}
