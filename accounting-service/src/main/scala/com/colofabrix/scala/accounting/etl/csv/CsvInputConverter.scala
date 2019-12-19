package com.colofabrix.scala.accounting.etl.csv

import java.io.File
import com.colofabrix.scala.accounting.etl._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.AValidation._
import com.colofabrix.scala.accounting.etl.csv.CsvReader
import com.colofabrix.scala.accounting.etl.RecordConverter._
import kantan.csv.ops.csvRows
import com.colofabrix.scala.accounting.etl.inputs.Barclays.BarclaysCsvFile
import shapeless._
import shapeless.syntax.std.tuple._

class CsvInputConverter[T <: InputTransaction](
    reader: CsvReader,
    implicit val converter: RecordConverter[T],
) extends InputConverter[File, T] {

  def ingestInput(input: File): AValidated[T] = {
    val validatedFile = reader.read(input)
    validatedFile.traverse { csvRows =>
      csvRows.map { row =>
        converter.convert(row)(BarclaysCsvFile.parsers)
      }
    }
    ???
  }

}
