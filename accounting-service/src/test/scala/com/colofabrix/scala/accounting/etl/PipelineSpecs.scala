package com.colofabrix.scala.accounting.etl

import com.colofabrix.scala.accounting.etl.csv.CsvProcessor
import com.colofabrix.scala.accounting.etl.inputs._
import com.colofabrix.scala.accounting.model._

/** Barclays */
class BarclaysSpecs extends PipelineSpecsDefs[BarclaysTransaction] with BarclaysTestData {
  private val instance = new BarclaysInputProcessor()
  implicit val processor: CsvProcessor[BarclaysTransaction] = instance
}

/** Halifax */
class HalifaxSpecs extends PipelineSpecsDefs[HalifaxTransaction] with HalifaxTestData {
  private val instance = new HalifaxInputProcessor()
  implicit val processor: CsvProcessor[HalifaxTransaction] = instance
}

/** Starling */
class StarlingSpecs extends PipelineSpecsDefs[StarlingTransaction] with StarlingTestData {
  private val instance = new StarlingInputProcessor()
  implicit val processor: CsvProcessor[StarlingTransaction] = instance
}

/** American Express */
class AmexSpecs extends PipelineSpecsDefs[AmexTransaction] with AmexTestData {
  private val instance = new AmexInputProcessor()
  implicit val processor: CsvProcessor[AmexTransaction] = instance
}
