package com.colofabrix.scala.accounting.etl.inputs

/**
 * Contains instances of all the input processors so that they don't need
 * to be initialized every time
 */
object InputInstances {

  val barclaysInput: BarclaysCsvInput = new BarclaysCsvInput()
  val halifaxInput: HalifaxCsvInput   = new HalifaxCsvInput()
  val starlingInput: StarlingCsvInput = new StarlingCsvInput()
  val amexInput: AmexCsvInput         = new AmexCsvInput()

}
