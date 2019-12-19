package com.colofabrix.scala.accounting.etl

import cats.data.Kleisli
import cats.implicits._
import com.colofabrix.scala.accounting.utils.AValidation._

object InputDefinitions {

  /** A raw record that comes from the input */
  type RawRecord = List[String]

  /** An input as collection of RawRecords */
  type RawInput = List[RawRecord]

}
