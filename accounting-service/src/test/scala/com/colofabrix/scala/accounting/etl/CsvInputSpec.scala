package com.colofabrix.scala.accounting.etl

import com.colofabrix.scala.accounting.model._

class BarclaysInputConversion extends InputConversionSpec[BarclaysTransaction] with BarclaysTestData
class HalifaxInputConversion  extends InputConversionSpec[HalifaxTransaction] with HalifaxTestData
class StarlingInputConversion extends InputConversionSpec[StarlingTransaction] with StarlingTestData
class AmexInputConversion     extends InputConversionSpec[AmexTransaction] with AmexTestData
