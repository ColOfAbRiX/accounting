package com.colofabrix.scala.accounting.etl

import fs2.Pure
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._
import cats.effect.IO

object definitions {

  /** A raw record that comes from the input */
  type RawRecord = List[String]

  /** An input as collection of RawRecords */
  type RawInput[F[_]] = fs2.Stream[F, RawRecord]

  /** An input as collection of RawRecords */
  type VRawInput[F[_]] = fs2.Stream[F, AValidated[RawRecord]]

}
