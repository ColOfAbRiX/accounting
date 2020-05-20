package com.colofabrix.scala.accounting.gatling

import io.gatling.core.Predef._
import io.gatling.core.structure._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import scala.concurrent.duration._
import scala.language.postfixOps

class AccountingSimulation extends Simulation {

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("http://localhost:8001")
    .shareConnections

  val scn: ScenarioBuilder = scenario("ETL Service Simulation")
    .exec(
      EtlServiceScenarios.supportInputs,
      EtlServiceScenarios.convertRecord,
      EtlServiceScenarios.convertRecords,
      EtlServiceScenarios.queryInputTypeAndConvertRecords,
    )

  val population: PopulationBuilder = scn
    .pause(5.seconds)
    .inject(
      rampUsersPerSec(1) to (800) during (30.seconds) randomized,
      constantUsersPerSec(800) during (30.seconds) randomized,
      rampUsersPerSec(800) to (1) during (30.seconds) randomized,
    )

  setUp(population)
    .protocols(httpProtocol)

}
