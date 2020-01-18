package com.colofabrix.scala.accounting.etl.api

import sttp.tapir.redoc.http4s.RedocHttp4s
import sttp.tapir.openapi.circe.yaml._

object Routes {

  val redocRoute: RedocHttp4s = {
    new RedocHttp4s("Accounting ETL API", Endpoints.openApiDocsEndpoint.toYaml)
  }

}
