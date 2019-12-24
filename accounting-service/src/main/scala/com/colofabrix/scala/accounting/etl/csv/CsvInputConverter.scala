package com.colofabrix.scala.accounting.etl.csv

import cats._
import cats.implicits._
import java.io.File
import com.colofabrix.scala.accounting.etl._
import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._
import cats.data.Validated.Valid
import cats.data.Validated.Invalid

/**
 * Processes a CSV file like filtering bad rows and converting them to case classes
 */
trait CsvProcessor[+T <: InputTransaction] {
  /** Converts a Csv row into a BankTransaction */
  def filterFile(file: RawInput): RawInput

  /** Converts a Csv row */
  def convertRow(row: RawRecord): AValidated[T]

  //  UTILITIES

  /** Drops the header of the input */
  def dropHeader(input: RawInput): RawInput = input.drop(1)
  /** Drops the empty records */
  def dropEmpty(input: RawInput): RawInput = {
    input.filter { record =>
      record.filter { cell =>
        Option(cell).map(_.trim.nonEmpty).getOrElse(false)
      }.nonEmpty
    }
  }
}

/**
 * Converts a CSV input into transactions
 */
class CsvInputConverter[+T <: InputTransaction](reader: CsvReader, processor: CsvProcessor[T])
    extends InputConverter[T] {

  def traverse[F[_]: Applicative, A, B](as: List[A])(f: A => F[B]): F[List[B]] =
    as.foldRight(Applicative[F].pure(List.empty[B])) { (a: A, acc: F[List[B]]) =>
      val fb: F[B] = f(a)
      Applicative[F].map2(fb, acc)(_ :: _)
    }

  /** Processes the entire content provided by the Input Reader */
  def ingestInput: AValidated[List[T]] = {
    // FIXME: Why validated don't accumulate errors??
    reader.read match {
      case Valid(rawInput) =>
        val step1 = processor
          .filterFile(rawInput)
          .map { filtered =>
            processor.convertRow(filtered)
          }

        val step2 = step1.foldRight(List.empty[T].aValid) {
          case (Valid(a), Valid(b))       => Valid(a :: b)
          case (Invalid(ia), Invalid(ib)) => Invalid(ia ++ ib)
          case (i @ Invalid(a), _)        => i
          case (_, i @ Invalid(b))        => i
        }

        step2

      case i @ Invalid(_) => i
    }

    // for {
    //   rawInput     <- reader.read
    //   filtered     <- processor.filterFile(rawInput).aValid
    //   transactions <- filtered.map(processor.convertRow).sequence
    // } yield {
    //   transactions
    // }
  }

}
