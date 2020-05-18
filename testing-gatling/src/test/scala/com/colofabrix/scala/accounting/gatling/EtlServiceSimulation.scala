package com.colofabrix.scala.accounting.gatling

import io.gatling.core.Predef._
import io.gatling.core.structure.{ ChainBuilder, PopulationBuilder, ScenarioBuilder }
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import scala.concurrent.duration._
import scala.language.postfixOps
import io.gatling.core.feeder.FeederBuilderBase

class AccountingSimulation extends Simulation {

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("http://localhost:8001")
    .doNotTrackHeader("1")

  val scn: ScenarioBuilder = scenario("ETL Service Simulation").exec(
    EtlService.supportInputs,
    EtlService.convertRecord,
  )

  val population: PopulationBuilder = scn
    .pause(5.seconds)
    .inject(
      atOnceUsers(10),
      rampUsersPerSec(1) to (100) during (30.seconds) randomized,
    )

  setUp(population)
    .protocols(httpProtocol)

}

object EtlService {
  val barclaysRecords = List(
    """"	null",10/01/2019,20-32-06 13152170,-1.7,PAYMENT,First Line           ON 09 JAN          CLP""",
    """"	null",10/01/2019,20-32-06 13152170,-52,PAYMENT,TRAINLINE             ON 08 JAN          BCC""",
    """"	null",10/01/2019,20-32-06 13152170,-29.95,PAYMENT,TRAINLINE             ON 09 JAN          BCC""",
    """"	null",11/01/2019,20-32-06 13152170,-2.9,PAYMENT,TFL TRAVEL CH         ON 10 JAN          CLP""",
    """"	null",11/01/2019,20-32-06 13152170,-9.34,PAYMENT,BEST FOOD CENTRE      ON 10 JAN          CLP""",
    """"	null",14/01/2019,20-32-06 13152170,-1.5,PAYMENT,TFL TRAVEL CH         ON 13 JAN          CLP""",
    """"	null",14/01/2019,20-32-06 13152170,-7.4,PAYMENT,BEST FOOD CENTRE      ON 13 JAN          CLP""",
    """"	null",14/01/2019,20-32-06 13152170,-2.4,PAYMENT,TFL TRAVEL CH         ON 12 JAN          CLP""",
    """"	null",14/01/2019,20-32-06 13152170,-2.6,PAYMENT,MAE + HARVEY          ON 12 JAN          CLP""",
    """"	null",14/01/2019,20-32-06 13152170,-7.65,PAYMENT,SAMUEL TEPO           ON 12 JAN          BCC""",
  )

  val halifaxRecords = List(
    "31/12/2019,01/01/2020,10227458,CRV*LA CRAFT            ,37.25",
    "30/12/2019,31/12/2019,10243403,CRV*LEFFE POP UP PIER   ,5.21",
    "30/12/2019,31/12/2019,10243402,CRV*LEFFE POP UP PIER   ,9.83",
    "30/12/2019,31/12/2019,10341611,PAYPAL *SHUTTLERNBO     ,17.93",
    "29/12/2019,30/12/2019,10501474,CRV*SORIANI PASTICCERI  ,5.84",
    "29/12/2019,30/12/2019,10501236,CRV*SUPERMERCATO A amp  ,5.05",
    """28/12/2019,30/12/2019,10851087,"CRV*ZAMPANO' MANGIARE,  ",5.16""",
    "27/12/2019,30/12/2019,10851371,CRV*SPIAGGIA 23 CESENA  ,17.5",
    "27/12/2019,30/12/2019,10202513,CRV*40241 IPMATIC       ,26.26",
    "27/12/2019,30/12/2019,10190222,CRV*FOB                 ,16.65",
  )

  val starlingRecords = List(
    """01/03/2019,COLONNA F,TOP UP STARLING,FASTER PAYMENT,100,100,INCOME""",
    """04/03/2019,Dominion Brewery C Colchester,IZ *DOMINION BREWERY C Colchester    GBR,CONTACTLESS,-8,92,LIFESTYLE""",
    """04/03/2019,Sainsbury's,SAINSBURYS SACAT 0768  COLCHESTER    GBR,CONTACTLESS,-3.7,88.3,GROCERIES""",
    """04/03/2019,Tesco,TESCO STORES 4396      COLCHESTER    GBR,CONTACTLESS,-5.07,83.23,GROCERIES""",
    """04/03/2019,TfL,TfL Travel Charge      TFL.gov.uk/CP GBR,CONTACTLESS,-1.5,81.73,TRANSPORT""",
    """04/03/2019,Dominion Brewery C Colchester,IZ *DOMINION BREWERY C Colchester    GBR,CONTACTLESS,-5.3,76.43,LIFESTYLE""",
    """04/03/2019,Dominion Brewery C Colchester,IZ *DOMINION BREWERY C Colchester    GBR,CONTACTLESS,-5.25,71.18,LIFESTYLE""",
    """04/03/2019,ALDI,ALDI STORES LIMITED    COLCHESTER    GBR,CONTACTLESS,-2.74,68.44,GROCERIES""",
    """05/03/2019,Sainsbury's,SAINSBURYS SACAT 4073  LONDON        GBR,CONTACTLESS,-3.75,64.69,GROCERIES""",
    """05/03/2019,TfL,TFL TRAVEL CH\VICTORIA STREET\TFL.GOV.UK/CP\SW1H 0TL     GBR,CONTACTLESS,-3,61.69,TRANSPORT""",
  )

  val amexRecords = List(
    """31 Dec 2019,EASYJET*EASYJET K11MRP6 LUTON,Travel,97.00""",
    """29 Dec 2019,PAYMENT RECEIVED - THANK YOU,,-794.28""",
    """18 Dec 2019,PP*BEER HAWK HARROGATE,General Purchases,9.30""",
    """18 Dec 2019,TFL TRAVEL CHARGE TFL.GOV.UK/CP,Travel,6.30""",
    """18 Dec 2019,TRAINLINE Trainline LONDON,Travel,31.05""",
    """17 Dec 2019,BOOTS THE CHEMIST LONDON,General Purchases,3.00""",
    """17 Dec 2019,MARKS & SPENCER SOUT LONDON,General Purchases,1.75""",
    """17 Dec 2019,TFL TRAVEL CHARGE TFL.GOV.UK/CP,Travel,6.80""",
    """16 Dec 2019,IZ *DOMINION BREWERY CO COLCHESTER,General Purchases,8.00""",
    """16 Dec 2019,TFL TRAVEL CHARGE TFL.GOV.UK/CP,Travel,1.50""",
  )

  val records = Map(
    "barclays" -> barclaysRecords,
    "halifax"  -> halifaxRecords,
    "starling" -> starlingRecords,
    "amex"     -> amexRecords,
  )

  val inputTypes = Stream("barclays", "halifax", "starling", "amex")

  val inputType = scala.util.Random.shuffle(inputTypes).head

  val feeder = for {
    round <- Stream.continually(records(inputType))
    body  <- round.toStream
  } yield {
    Map("inputType" -> inputType, "body" -> body)
  }

  val supportInputs: ChainBuilder =
    exec(http("supported-inputs").get("/api/v1.0/supported-inputs"))
      .feed(feeder.iterator)

  val convertRecord: ChainBuilder = exec(
    http("convert-record")
      .get("/api/v1.0/convert-record")
      .queryParam("inputType", "${inputType}"),
  )
}
