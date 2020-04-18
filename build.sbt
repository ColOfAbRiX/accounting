import Dependencies._
import AllProjectsKeys.autoImport._

// Scala version
lazy val ScalaLangVersion = "2.13.1"

// General
Global / onChangedBuildSource := ReloadOnSourceChanges
ThisBuild / organization := "com.colofabrix.scala.accounting"
ThisBuild / scalaVersion := ScalaLangVersion
ThisBuild / turbo := true
ThisBuild / developers := List(
  Developer("ColOfAbRiX", "Fabrizio Colonna", "@ColOfAbRiX", url("http://github.com/ColOfAbRiX")),
)

// Compiler options
ThisBuild / scalacOptions ++= Compiler.TpolecatOptions ++ Seq("-P:splain:all")

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
  ).flatten ++ Seq(
  BetterMonadicForPlugin,
  KindProjectorPlugin,
  PPrintDep,
  SplainPlugin,
  WartremoverPlugin,
)

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
      LoggingBundle,
    ).flatten ++ Seq(
      CatsCoreDep,
      ScalaTestCatsDep,
      FS2CoreDep,
      ScalaTestDep,
    ),
  )

// Business Model project
lazy val model = project
  .in(file("module-model"))
  .settings(
    name := "model",
    libraryDependencies ++= Seq(
      EnumeratumBundle,
      RefinedBundle,
    ).flatten ++ Seq(),
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
      CatsBundle,
      ChimneyBundle,
      CirceBundle,
      ConfigBundle,
      EnumeratumBundle,
      Http4sBundle,
      KantanCsvBundle,
      LoggingBundle,
      RefinedBundle,
      TapirBundle,
    ).flatten ++ Seq(
      CirceGenericDep,
      FS2CoreDep,
      ScalaTestDep,
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
      CatsBundle,
      ChimneyBundle,
      CirceBundle,
      ConfigBundle,
      EnumeratumBundle,
      Http4sBundle,
      KantanCsvBundle,
      LoggingBundle,
      TapirBundle,
    ).flatten ++ Seq(
      CirceGenericDep,
      FS2CoreDep,
      ScalaTestDep,
      ShapelessDep,
    ),
  )
