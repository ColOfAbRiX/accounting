import Dependencies._
import AllProjectsKeys.autoImport._

// Scala version
lazy val ScalaLangVersion = "2.13.0"
Global / scalaVersion := ScalaLangVersion

// General
ThisBuild / organization := "com.colofabrix.scala.accounting"

// Compiler options
ThisBuild / scalacOptions ++= Compiler.TpolecatOptions ++ Seq("-P:splain:all")
ThisBuild / developers := List(
  Developer("ColOfAbRiX", "Fabrizio Colonna", "@ColOfAbRiX", url("http://github.com/ColOfAbRiX")),
)

// GIT version information
ThisBuild / dynverVTagPrefix := false

// Wartremover
ThisBuild / wartremoverExcluded ++= (baseDirectory.value * "**" / "src" / "test").get
ThisBuild / wartremoverErrors ++= Warts.allBut(
  Wart.Any,
  Wart.Nothing,
  Wart.Overloading,
  Wart.ToString,
)

// Scalafmt
ThisBuild / scalafmtOnCompile := true

// Global dependencies and compiler plugins
ThisBuild / libraryDependencies ++= Seq(
  BetterMonadicForPlugin,
  KindProjectorPlugin,
  PPrintDep,
  SlainPlugin,
  WartremoverPlugin,
) ++ Seq(
  LoggingBundle,
).flatten

//  PROJECTS  //

// Root project
lazy val rootProject: Project = project
  .in(file("."))
  .settings(
    name := "accounting",
    description := "Accounting",
  )
  .aggregate(
    etlService,
    transactionsService,
  )

// Utils project
lazy val utils = project
  .in(file("module-utils"))
  .settings(
    name := "utils",
    description := "Global Utilities",
    libraryDependencies ++= Seq(
      CatsCoreDep,
      CatsScalaTestDep,
      FS2CoreDep,
      ScalatestDep,
    ),
  )

// Business Model project
lazy val model = project
  .in(file("module-model"))
  .settings(
    name := "model",
    libraryDependencies ++= Seq(),
  )

// ETL Service project
lazy val etlService = project
  .in(file("module-etl-service"))
  .dependsOn(
    utils,
    model,
  )
  .enablePlugins(BuildInfoPlugin)
  .settings(
    name := "etl-service",
    description := "Accounting ETL Service",
    buildInfoPackage := projectPackage.value,
    buildInfoKeys ++= projectBuildInfo.value,
    libraryDependencies ++= Seq(
      EnumeratumBundle,
      HttpServiceBundle,
      KantanCsvBundle,
    ).flatten ++ Seq(
      ScalatestDep,
      ShapelessDep,
    ),
  )

// Transactions service
lazy val transactionsService = project
  .in(file("module-transactions-service"))
  .dependsOn(
    utils,
    model,
  )
  .enablePlugins(BuildInfoPlugin)
  .settings(
    name := "transactions-service",
    description := "Accounting Transactions Service",
    buildInfoPackage := projectPackage.value,
    buildInfoKeys ++= projectBuildInfo.value,
    libraryDependencies ++= Seq(
      EnumeratumBundle,
      HttpServiceBundle,
      KantanCsvBundle,
    ).flatten ++ Seq(
      ScalatestDep,
      ShapelessDep,
    ),
  )
