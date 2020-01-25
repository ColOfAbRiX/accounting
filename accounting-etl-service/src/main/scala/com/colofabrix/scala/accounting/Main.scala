package com.colofabrix.scala.accounting

import cats.effect._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.Router
import org.http4s.syntax.kleisli._

import scala.concurrent.ExecutionContext
import com.colofabrix.scala.accounting.etl.api._
import com.colofabrix.scala.accounting.utils.ExecutionContexts

@SuppressWarnings(Array("org.wartremover.warts.All"))
object MultipleEndpointsDocumentationHttp4sServer extends App {

  implicit val ec: ExecutionContext           = ExecutionContexts.computePool
  implicit val contextShift: ContextShift[IO] = IO.contextShift(ec)
  implicit val timer: Timer[IO]               = IO.timer(ec)

  // starting the server
  BlazeServerBuilder[IO]
    .bindHttp(8080, "localhost")
    .withHttpApp(Router("/" -> Routes.allRoutes).orNotFound)
    .resource
    .use { _ =>
      IO {
        scala.io.StdIn.readLine()
      }
    }
    .unsafeRunSync()
}
