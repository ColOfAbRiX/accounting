import Dependencies._
import Compiler._

// General
val ScalaVersion      = "2.13.0"
val AccountingVersion = "0.1.0-SNAPSHOT"

// Compiler options
scalacOptions in ThisBuild ++= TpolecatOptions

// Wartremover
wartremoverExcluded in ThisBuild ++= (baseDirectory.value * "**" / "src" / "test").get
wartremoverErrors in ThisBuild ++= Warts.allBut(
  Wart.Any,
  Wart.Nothing,
  Wart.Overloading,
  Wart.ToString,
)

// Standardize formatting
scalafmtOnCompile in ThisBuild := true

// Global dependencies and compiler plugins
libraryDependencies in ThisBuild ++= Seq(
  BetterMonadicForPlugin,
  KindProjectorPlugin,
  PPrintDep,
  WartremoverPlugin,
) ++ Seq(
  LoggingBundle,
  SilencerBundle,
).flatten

//  - - - - - - - - - - - - - - - - - //

// Root project
lazy val accountingRoot: Project = project
  .in(file("."))
  .settings(
    organization := "com.colofabrix.scala.accounting",
    name := "accounting",
    version := AccountingVersion,
    scalaVersion := ScalaVersion,
    libraryDependencies ++= Seq(),
  )
  .aggregate(
    etlService,
  )

// Utils project
lazy val utils = project
  .in(file("accounting-utils"))
  .settings(
    organization := "com.colofabrix.scala.accounting",
    name := "accounting-utils",
    version := AccountingVersion,
    scalaVersion := ScalaVersion,
    libraryDependencies ++= Seq(
      CatsCoreDep,
      CatsScalaTestDep,
      FS2CoreDep,
      ScalatestDep,
    ),
  )

// Business Model project
lazy val model = project
  .in(file("accounting-model"))
  .settings(
    organization := "com.colofabrix.scala.accounting",
    name := "accounting-model",
    version := AccountingVersion,
    scalaVersion := ScalaVersion,
    libraryDependencies ++= Seq(),
  )

// ETL Service project
lazy val etlService = project
  .in(file("accounting-etl-service"))
  .dependsOn(
    utils,
    model,
  )
  .settings(
    organization := "com.colofabrix.scala.accounting",
    name := "accounting-etl-service",
    version := AccountingVersion,
    scalaVersion := ScalaVersion,
    libraryDependencies ++= Seq(
      CirceGenericDep,
      FS2CoreDep,
      PureconfigDep,
      ScalatestDep,
      ShapelessDep,
    ) ++ Seq(
      CatsBundle,
      KantanCsvBundle,
      Http4sBundle,
      TapirBundle,
    ).flatten,
  )
