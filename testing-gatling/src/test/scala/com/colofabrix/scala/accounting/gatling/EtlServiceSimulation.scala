package com.colofabrix.scala.accounting.gatling

import io.gatling.core.Predef._
import io.gatling.core.structure.{ ChainBuilder, PopulationBuilder, ScenarioBuilder }
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import scala.concurrent.duration._

class AccountingSimulation extends Simulation {

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("http://localhost:8001")
    .doNotTrackHeader("1")

  val scn: ScenarioBuilder = scenario("ETL Service Simulation").exec(
    EtlService.supportInputs,
    EtlService.convertRecord,
  )

  val population: PopulationBuilder = scn
    .inject(heavisideUsers(100) during 10.seconds)
    .uniformPauses(3.seconds)

  setUp(population).protocols(httpProtocol)

}

object EtlService {

  val supportInputs: ChainBuilder = exec {
    http("supported-inputs")
      .get("/api/v1.0/supported-inputs")
  }

  val convertRecord: ChainBuilder = exec {
    http("convert-record")
      .get("/api/v1.0/convert-record")
      .queryParam("inputType", "barclays")
  }

}
