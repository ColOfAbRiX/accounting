package com.colofabrix.scala.accounting.etl.csv

import cats.data._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.inputs._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._

trait Transformer[T <: InputTransaction] {
  def transform(input: T): Transaction
}

object Transformer {
  /** Converts a given stream into transactions */
  // def apply[T <: InputTransaction]()
  // def fromStream[T <: InputTransaction](stream: VRawInput[IO])(implicit converter: Loader[T]) = {
  //   stream.through(converter.load)
  // }

  implicit val barclaysTransformer = new Transformer[BarclaysTransaction] {
    def transform(input: BarclaysTransaction): Transaction = {
      new BarclaysCsvProcessor().transform(input)
    }
  }

  implicit val halifaxTransformer = new Transformer[HalifaxTransaction] {
    def transform(input: HalifaxTransaction): Transaction = {
      new HalifaxCsvProcessor().transform(input)
    }
  }

  implicit val starlingTransformer = new Transformer[StarlingTransaction] {
    def transform(input: StarlingTransaction): Transaction = {
      new StarlingCsvProcessor().transform(input)
    }
  }

  implicit val amexTransformer = new Transformer[AmexTransaction] {
    def transform(input: AmexTransaction): Transaction = {
      new AmexCsvProcessor().transform(input)
    }
  }
}
