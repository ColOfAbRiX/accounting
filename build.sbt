import Compiler._
import Dependencies._

// General
lazy val ScalaVersion      = "2.13.0"
lazy val AccountingVersion = "0.8.0-SNAPSHOT"
lazy val ProjectNamespace  = "com.colofabrix.scala.sample"

// Compiler options
scalacOptions in ThisBuild ++= TpolecatOptions

// GIT versioning
enablePlugins(GitVersioning)

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
).flatten

//  PROJECTS  //

// Shared projects settings
lazy val sharedSettings = Seq(
  organization := ProjectNamespace,
  version := AccountingVersion,
  scalaVersion := ScalaVersion,
)

// Root project
lazy val accountingRoot: Project = project
  .in(file("."))
  .settings(
    name := "accounting",
    sharedSettings,
    libraryDependencies ++= Seq(),
  )
  .aggregate(
    etlService, transactionsDbService
  )

// Utils project
lazy val utils = project
  .in(file("accounting-utils"))
  .settings(
    name := "utils",
    sharedSettings,
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
    name := "model",
    sharedSettings,
    libraryDependencies ++= Seq(),
  )

// ETL Service project
lazy val etlService = project
  .in(file("accounting-etl-service"))
  .dependsOn(
    utils,
    model,
  )
  .enablePlugins(BuildInfoPlugin)
  .settings(
    name := "etl-service",
    sharedSettings,
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := organization.value,
    libraryDependencies ++= Seq(
      HttpServiceBundle,
      KantanCsvBundle,
    ).flatten ++ Seq(
      ScalatestDep,
      ShapelessDep,
    ),
  )

// Transactions DB service
lazy val transactionsDbService = project
  .in(file("accounting-trnxs-db-service"))
  .dependsOn(
    utils,
    model,
  )
  .enablePlugins(BuildInfoPlugin)
  .settings(
    name := "transactions-db-service",
    sharedSettings,
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := organization.value,
    libraryDependencies ++= Seq(
      HttpServiceBundle,
      KantanCsvBundle,
    ).flatten ++ Seq(
      ScalatestDep,
      ShapelessDep,
    ),
  )
