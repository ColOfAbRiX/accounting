package com.colofabrix.scala.accounting.etl

import cats.implicits._
import com.colofabrix.scala.accounting.etl.FieldConverter._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.model.InputTransaction
import com.colofabrix.scala.accounting.utils.validation._
import shapeless.{ Generic, HList, HNil, Poly2 }
import shapeless.ops.hlist.RightFolder
import shapeless.UnaryTCConstraint.*->*

/**
 * Represents an object that can convert inputs into type T
 */
trait RecordConverter[T <: InputTransaction] {

  // -- The following has been adapted from https://stackoverflow.com/a/25316124 -- //

  // The "trick" here is to pass the record as the initial value of the fold and carry it along
  // during the computation. Inside the computation we apply a parser using record as parameter and
  // then we append it to the accumulator.

  // format: off
  private type Accumulator[A <: HList] = (RawRecord, AValidated[A])

  private object ApplyRecord extends Poly2 {
    implicit def folder[T, V <: HList] = at[FieldBuilder[T], Accumulator[V]] {
      case (recordParser, (record, accumulator)) =>
        val parsed = recordParser(record)
        val next = (accumulator, parsed).mapN((v, t) => t :: v)
        (record, next)
    }
  }

  // UnaryTCConstraint taken from here: https://mpilquist.github.io/blog/2013/06/09/scodec-part-3/

  def convert[
    HParsers <: HList : *->*[FieldBuilder]#λ,
    HParsed <: HList](
      record: RawRecord)(
      parsers: HParsers)(
        implicit
        folder: RightFolder.Aux[HParsers, Accumulator[HNil], ApplyRecord.type, Accumulator[HParsed]],
        gen: Generic.Aux[T, HParsed],
  ): AValidated[T] = {
    val tmp = parsers
      .foldRight((record, (HNil: HNil).aValid))(ApplyRecord)._2
      .map(gen.from)
    tmp
  }
  // format: on

}

object RecordConverter {
  implicit def recordConverter[T <: InputTransaction] = new RecordConverter[T] {}
}
