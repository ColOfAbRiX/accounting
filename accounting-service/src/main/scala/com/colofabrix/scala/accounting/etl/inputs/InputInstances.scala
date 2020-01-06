package com.colofabrix.scala.accounting.etl.inputs

/**
 * Contains instances of all the input processors so that they don't need
 * to be initialized every time
 */
object InputInstances {

  val barclaysInput: BarclaysInputProcessor = new BarclaysInputProcessor()
  val halifaxInput: HalifaxInputProcessor   = new HalifaxInputProcessor()
  val starlingInput: StarlingInputProcessor = new StarlingInputProcessor()
  val amexInput: AmexInputProcessor         = new AmexInputProcessor()

}
