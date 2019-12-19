package com.colofabrix.scala.accounting.etl

import cats.data.Kleisli
import cats.implicits._
import InputDefinitions._
import FieldConverter.FieldBuilder
import com.colofabrix.scala.accounting.utils.AValidation._
import shapeless.ops.hlist.RightFolder
import shapeless.{ Generic, HList, HNil, Poly2 }
import shapeless.UnaryTCConstraint.*->*
import com.colofabrix.scala.accounting.model.InputTransaction

/**
  * Represents an object that can convert inputs into type T
  */
trait RecordConverter[T <: InputTransaction] {

  // -- The following has been adapted from https://stackoverflow.com/a/25316124 -- //

  // The "trick" here is to pass the row as the initial value of the fold and carry it along
  // during the computation. Inside the computation we apply a parser using row as parameter and
  // then we append it to the accumulator.

  // format: off
  type Accumulator[A <: HList] = (RawRecord, AValidated[A])

  object ApplyRecord extends Poly2 {
    implicit def folder[T, V <: HList] = at[FieldBuilder[T], Accumulator[V]] {
      case (rowParser, (row, accumulator)) =>
        val parsed = rowParser(row)
        val next = (accumulator, parsed).mapN((v, t) => t :: v)
        (row, next)
    }
  }

  // UnaryTCConstraint taken from here: https://mpilquist.github.io/blog/2013/06/09/scodec-part-3/

  def convert[
    HParsers <: HList : *->*[FieldBuilder]#λ,
    HParsed <: HList](
      row: RawRecord)(
      parsers: HParsers)(
        implicit
        folder: RightFolder.Aux[HParsers, Accumulator[HNil], ApplyRecord.type, Accumulator[HParsed]],
        gen: Generic.Aux[T, HParsed],
  ): AValidated[T] = {
    parsers
      .foldRight((row, (HNil: HNil).aValid))(ApplyRecord)._2
      .map(gen.from)
  }
  // format: on

}

object RecordConverter {
  implicit def recordConverter[T <: InputTransaction] = new RecordConverter[T] {}
}
