package com.colofabrix.scala.accounting.gatling

import io.gatling.core.feeder._
import io.gatling.core.Predef._
import io.gatling.core.structure._
import io.gatling.http.Predef._
import java.util.concurrent.ThreadLocalRandom
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random

object EtlServiceScenarios {
  import com.colofabrix.scala.accounting.gatling.data._

  /**
   * Test the "supported-inputs" endpoint
   */
  def supportInputs: ChainBuilder = exec(
    http("supported-inputs")
      .get("/api/v1.0/supported-inputs"),
  )

  /**
   * Test the "convert-record" endpoint
   */
  def convertRecord: ChainBuilder = {
    exec { session =>
      val inputTypes        = EtlServiceData.allRecords.keySet.toList
      val selectedInputType = inputTypes(ThreadLocalRandom.current.nextInt(inputTypes.length))
      val bodiesForInput    = EtlServiceData.allRecords(selectedInputType)
      val selectedBody      = bodiesForInput(ThreadLocalRandom.current.nextInt(bodiesForInput.length))
      session
        .set("inputType", selectedInputType)
        .set("body", selectedBody)

    } exec {
      http("convert-record")
        .get("/api/v1.0/convert-record")
        .queryParam("inputType", "${inputType}")
        .body(StringBody("${body}")),
    }
  }

  /**
   * Test the "convert-records" endpoint
   */
  def convertRecords: ChainBuilder = {
    exec { session =>
      val inputTypes        = EtlServiceData.allRecords.keySet.toList
      val selectedInputType = inputTypes(ThreadLocalRandom.current.nextInt(inputTypes.length))
      val selectedRecords   = EtlServiceData.allRecords(selectedInputType)
      val bodiesForInput    = Random.shuffle(Vector.fill(ThreadLocalRandom.current.nextInt(0, 100))(selectedRecords))
      session
        .set("inputType", selectedInputType)
        .set("body", bodiesForInput.mkString("\n"))

    }
    exec {
      http("convert-records")
        .get("/api/v1.0/convert-records")
        .queryParam("inputType", "${inputType}")
        .body(StringBody("${body}")),
    }
  }

  def queryInputTypeAndConvertRecords = {
    exec {
      http("supported-inputs")
        .get("/api/v1.0/supported-inputs")
    }
    pause(500 milliseconds)
    convertRecords
  }
}
