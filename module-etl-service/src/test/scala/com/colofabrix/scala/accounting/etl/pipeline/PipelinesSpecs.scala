package com.colofabrix.scala.accounting.etl.pipeline

import com.colofabrix.scala.accounting.etl.model._
import com.colofabrix.scala.accounting.etl.pipeline.data._
import com.colofabrix.scala.accounting.etl.pipeline.definitions.PipelineSpecsDefinition

/** Barclays tests */
class BarclaysPipelinesSpecs extends PipelineSpecsDefinition[BarclaysTransaction] with BarclaysTestData {
  runTests()
}

/** Halifax tests */
class HalifaxPipelinesSpecs extends PipelineSpecsDefinition[HalifaxTransaction] with HalifaxTestData {
  runTests()
}

/** Starling tests */
class StarlingPipelinesSpecs extends PipelineSpecsDefinition[StarlingTransaction] with StarlingTestData {
  runTests()
}

/** Amex tests */
class AmexPipelinesSpecs extends PipelineSpecsDefinition[AmexTransaction] with AmexTestData {
  runTests()
}
