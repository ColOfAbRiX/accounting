package com.colofabrix.scala.accounting.etl

import cats.data.Kleisli
import cats.implicits._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.model.InputTransaction
import com.colofabrix.scala.accounting.utils.validation._
import shapeless.{ Generic, HList, HNil, Poly2 }
import shapeless.ops.hlist.RightFolder
import shapeless.UnaryTCConstraint.*->*

/**
 * Represents an object that can convert Inputs records into Output types
 */
trait GenericConverter[Input, Output] {

  type GenericBuilder[A] = Kleisli[AValidated, Input, A]

  // -- The following has been adapted from https://stackoverflow.com/a/25316124 -- //

  // The "trick" here is to pass the record as the initial value of the fold and carry it along
  // during the computation. Inside the computation we apply a parser using record as parameter and
  // then we append it to the accumulator.

  // format: off
  private type Accumulator[A <: HList] = (Input, AValidated[A])

  private object ApplyRecord extends Poly2 {
    @SuppressWarnings(Array("org.wartremover.warts.ExplicitImplicitTypes"))
    implicit def folder[Output, V <: HList] = at[GenericBuilder[Output], Accumulator[V]] {
      case (recordParser, (record, accumulator)) =>
        val parsed = recordParser(record)
        val next = (accumulator, parsed).mapN((v, t) => t :: v)
        (record, next)
    }
  }

  // UnaryTCConstraint taken from here: https://mpilquist.github.io/blog/2013/06/09/scodec-part-3/

  def convertRecord[
    HParsers <: HList : *->*[GenericBuilder]#λ,
    HParsed <: HList](
      record: Input)(
      parsers: HParsers)(
        implicit
        folder: RightFolder.Aux[HParsers, Accumulator[HNil], ApplyRecord.type, Accumulator[HParsed]],
        gen: Generic.Aux[Output, HParsed],
  ): AValidated[Output] = {
    parsers
      .foldRight((record, (HNil: HNil).aValid))(ApplyRecord)._2
      .map(gen.from)
  }
  // format: on

}

/**
 * Specialization to convert RawRecords into InputTransaction
 */
trait RecordConverter[T <: InputTransaction] extends GenericConverter[RawRecord, T]
