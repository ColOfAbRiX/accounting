package com.colofabrix.scala.accounting.etl.pipeline

import com.colofabrix.scala.accounting.etl.definitions._
import com.colofabrix.scala.accounting.etl.inputs._
import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.etl.model.Config._
import com.colofabrix.scala.accounting.etl.model.Config.InputType._
import com.colofabrix.scala.accounting.etl.pipeline._
import com.colofabrix.scala.accounting.model._
import com.colofabrix.scala.accounting.utils.validation._
import fs2.Pure

/**
 * Pipeline instances to process an API input
 */
object ApiPipelineInstances {

  private[this] val barclaysInput: BarclaysApiInput = new BarclaysApiInput()
  private[this] val halifaxInput: HalifaxApiInput   = new HalifaxApiInput()
  private[this] val starlingInput: StarlingApiInput = new StarlingApiInput()
  private[this] val amexInput: AmexApiInput         = new AmexApiInput()

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

  def pipelineForType[T <: InputTransaction](inputType: InputType): VPipe[Pure, RawRecord, Transaction] = {
    inputType match {
      case BarclaysInputType => Pipeline[BarclaysTransaction]
      case HalifaxInputType  => Pipeline[HalifaxTransaction]
      case StarlingInputType => Pipeline[StarlingTransaction]
      case AmexInputType     => Pipeline[AmexTransaction]
    }
  }

}

/**
 * Pipeline instances to process a CSV file input
 */
object CsvFilePipelineInstances {

  private[this] val barclaysInput: BarclaysCsvInput = new BarclaysCsvInput()
  private[this] val halifaxInput: HalifaxCsvInput   = new HalifaxCsvInput()
  private[this] val starlingInput: StarlingCsvInput = new StarlingCsvInput()
  private[this] val amexInput: AmexCsvInput         = new AmexCsvInput()

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

  def pipelineForType[T <: InputTransaction](inputType: InputType): VPipe[Pure, RawRecord, Transaction] =
    inputType match {
      case BarclaysInputType => Pipeline[BarclaysTransaction]
      case HalifaxInputType  => Pipeline[HalifaxTransaction]
      case StarlingInputType => Pipeline[StarlingTransaction]
      case AmexInputType     => Pipeline[AmexTransaction]
    }

}
