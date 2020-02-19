package com.colofabrix.scala.accounting.etl

import com.colofabrix.scala.accounting.etl.inputs._
import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.etl.pipeline._

// format: off

/** Barclays */
class BarclaysSpecs extends PipelineSpecsDefs[BarclaysTransaction] with BarclaysTestData {
  private[this] val instance = new BarclaysCsvInput()
  implicit val processor: InputProcessor[BarclaysTransaction] = instance
  implicit val cleaner: Cleaner[BarclaysTransaction] = instance
}

/** Halifax */
class HalifaxSpecs extends PipelineSpecsDefs[HalifaxTransaction] with HalifaxTestData {
  private[this] val instance = new HalifaxCsvInput()
  implicit val processor: InputProcessor[HalifaxTransaction] = instance
  implicit val cleaner: Cleaner[HalifaxTransaction] = instance
}

/** Starling */
class StarlingSpecs extends PipelineSpecsDefs[StarlingTransaction] with StarlingTestData {
  private[this] val instance = new StarlingCsvInput()
  implicit val processor: InputProcessor[StarlingTransaction] = instance
  implicit val cleaner: Cleaner[StarlingTransaction] = instance
}

/** American Express */
class AmexSpecs extends PipelineSpecsDefs[AmexTransaction] with AmexTestData {
  private[this] val instance = new AmexCsvInput()
  implicit val processor: InputProcessor[AmexTransaction] = instance
  implicit val cleaner: Cleaner[AmexTransaction] = instance
}

// format: on
