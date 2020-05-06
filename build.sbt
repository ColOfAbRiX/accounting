import autoplugins.BuildEnvAutoPlugin.autoImport._
import autoplugins.DockerAutoPlugin.autoImport._
import dependencies.Dependencies._

// General
Global / onChangedBuildSource := ReloadOnSourceChanges
ThisBuild / organization := "com.colofabrix.scala.accounting"
ThisBuild / scalaVersion := ScalaLangVersion
ThisBuild / turbo := true
ThisBuild / developers := List(
  Developer("ColOfAbRiX", "Fabrizio Colonna", "@ColOfAbRiX", url("http://github.com/ColOfAbRiX")),
)

// Compiler options
ThisBuild / scalacOptions ++= (buildEnv.value match {
  case BuildEnv.Production =>
    sLog.value.log(sbt.util.Level.Info, "Using scalac optimizer for production build")
    Compiler.OptionsForOptimizer ++ Compiler.TpolecatOptions
  case _ =>
    Compiler.TpolecatOptions ++ Seq("-P:splain:all")
})

// GIT version information
ThisBuild / dynverVTagPrefix := false
ThisBuild / dynverSeparator := "-"

// Wartremover
ThisBuild / wartremoverExcluded ++= (baseDirectory.value * "**" / "src" / "test").get
ThisBuild / wartremoverErrors ++= Warts.allBut(
  Wart.Any,
  Wart.ImplicitParameter,
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
    description := "Global Accounting Utilities",
    // Dependencies
    bundledDependencies ++= Seq(
      LoggingBundle,
      TestingBundle,
    ),
    libraryDependencies ++= Seq(
      CatsCoreDep,
      FS2CoreDep,
    ),
  )

// Business Model project
lazy val model = project
  .in(file("module-model"))
  .settings(
    name := "model",
    description := "Accounting Shared Model",
    // Dependencies
    bundledDependencies ++= Seq(
      EnumeratumBundle,
      RefinedBundle,
    ),
  )

// ETL Service project
lazy val etlService = project
  .in(file("module-etl-service"))
  .dependsOn(
    utils % "compile->compile;test->test",
    model,
  )
  .enablePlugins(BuildInfoPlugin, DockerPlugin, JavaServerAppPackaging)
  .settings(
    name := "etl-service",
    description := "Accounting ETL Service",
    // Docker
    dockerExposedPorts ++= Seq(8001),
    dockerJavaProperties ++= Seq(
      "server.port=8001",
      "server.debug-mode=false",
    ),
    // Dependencies
    bundledDependencies ++= Seq(
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
      TestingBundle,
    ),
    libraryDependencies ++= Seq(
      CirceGenericDep,
      SimulacrumDep,
      FS2CoreDep,
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
  .enablePlugins(BuildInfoPlugin, DockerPlugin, JavaServerAppPackaging)
  .settings(
    name := "transactions-service",
    description := "Accounting Transactions Service",
    // Docker
    dockerExposedPorts ++= Seq(8002),
    dockerJavaProperties ++= Seq(
      "server.port=8002",
      "server.debug-mode=false",
    ),
    // Dependencies
    bundledDependencies ++= Seq(
      CatsBundle,
      ChimneyBundle,
      CirceBundle,
      ConfigBundle,
      EnumeratumBundle,
      Http4sBundle,
      KantanCsvBundle,
      LoggingBundle,
      TapirBundle,
      TestingBundle,
    ),
    libraryDependencies ++= Seq(
      CirceGenericDep,
      FS2CoreDep,
      ShapelessDep,
    ),
  )
