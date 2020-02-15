package com.colofabrix.scala.accounting.etl

import com.colofabrix.scala.accounting.utils.validation._
import fs2.Stream

object definitions {

  /** A raw record that comes from the input */
  type RawRecord = List[String]

  /** An input as collection of RawRecords */
  type RawInput[F[_]] = Stream[F, RawRecord]

  /** An input as collection of RawRecords */
  type VRawInput[F[_]] = Stream[F, AValidated[RawRecord]]

}
