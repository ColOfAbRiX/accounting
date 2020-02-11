package com.colofabrix.scala.accounting.etl.pipeline

import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.inputs._
import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.etl.model.Config._
import com.colofabrix.scala.accounting.etl.pipeline._
import com.colofabrix.scala.accounting.utils.validation._
import com.colofabrix.scala.accounting.model._

/**
 * Pipeline instances to process an API input
 */
object ApiPipelineInstances {

  private val barclaysInput: BarclaysApiInput = new BarclaysApiInput()
  private val halifaxInput: HalifaxApiInput   = new HalifaxApiInput()
  private val starlingInput: StarlingApiInput = new StarlingApiInput()
  private val amexInput: AmexApiInput         = new AmexApiInput()

  implicit val barclaysCleaner: Cleaner[BarclaysTransaction] = barclaysInput
  implicit val halifaxCleaner: Cleaner[HalifaxTransaction]   = halifaxInput
  implicit val starlingCleaner: Cleaner[StarlingTransaction] = starlingInput
  implicit val amexCleaner: Cleaner[AmexTransaction]         = amexInput

  implicit val barclaysProcessor: InputProcessor[BarclaysTransaction] = barclaysInput
  implicit val halifaxProcessor: InputProcessor[HalifaxTransaction]   = halifaxInput
  implicit val starlingProcessor: InputProcessor[StarlingTransaction] = starlingInput
  implicit val amexProcessor: InputProcessor[AmexTransaction]         = amexInput

  implicit val barclaysNormalizer: Normalizer[BarclaysTransaction] = barclaysInput
  implicit val halifaxNormalizer: Normalizer[HalifaxTransaction]   = halifaxInput
  implicit val starlingNormalizer: Normalizer[StarlingTransaction] = starlingInput
  implicit val amexNormalizer: Normalizer[AmexTransaction]         = amexInput

  def pipelineForType[T <: InputTransaction](inputType: InputType): VPipe[fs2.Pure, RawRecord, Transaction] =
    inputType match {
      case BarclaysInputType => Pipeline[BarclaysTransaction]
      case HalifaxInputType  => Pipeline[HalifaxTransaction]
      case StarlingInputType => Pipeline[StarlingTransaction]
      case AmexInputType     => Pipeline[AmexTransaction]
    }

}

/**
 * Pipeline instances to process a CSV file input
 */
object CsvFilePipelineInstances {

  private val barclaysInput: BarclaysCsvInput = new BarclaysCsvInput()
  private val halifaxInput: HalifaxCsvInput   = new HalifaxCsvInput()
  private val starlingInput: StarlingCsvInput = new StarlingCsvInput()
  private val amexInput: AmexCsvInput         = new AmexCsvInput()

  implicit val barclaysCleaner: Cleaner[BarclaysTransaction] = barclaysInput
  implicit val halifaxCleaner: Cleaner[HalifaxTransaction]   = halifaxInput
  implicit val starlingCleaner: Cleaner[StarlingTransaction] = starlingInput
  implicit val amexCleaner: Cleaner[AmexTransaction]         = amexInput

  implicit val barclaysProcessor: InputProcessor[BarclaysTransaction] = barclaysInput
  implicit val halifaxProcessor: InputProcessor[HalifaxTransaction]   = halifaxInput
  implicit val starlingProcessor: InputProcessor[StarlingTransaction] = starlingInput
  implicit val amexProcessor: InputProcessor[AmexTransaction]         = amexInput

  implicit val barclaysNormalizer: Normalizer[BarclaysTransaction] = barclaysInput
  implicit val halifaxNormalizer: Normalizer[HalifaxTransaction]   = halifaxInput
  implicit val starlingNormalizer: Normalizer[StarlingTransaction] = starlingInput
  implicit val amexNormalizer: Normalizer[AmexTransaction]         = amexInput

  def pipelineForType[T <: InputTransaction](inputType: InputType): VPipe[fs2.Pure, RawRecord, Transaction] =
    inputType match {
      case BarclaysInputType => Pipeline[BarclaysTransaction]
      case HalifaxInputType  => Pipeline[HalifaxTransaction]
      case StarlingInputType => Pipeline[StarlingTransaction]
      case AmexInputType     => Pipeline[AmexTransaction]
    }

}
