import Dependencies._
import AllProjectsKeys.autoImport._
import BuildEnvPlugin.autoImport.BuildEnv

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
    packageDescription := description.value,
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
    packageDescription := description.value,
    // Dependencies
    libraryDependencies ++= Seq(
      LoggingBundle,
      TestingBundle
    ).flatten ++ Seq(
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
    packageDescription := description.value,
    // Dependencies
    libraryDependencies ++= Seq(
      EnumeratumBundle,
      RefinedBundle,
    ).flatten ++ Seq(),
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
    packageDescription := description.value,
    // Build Info
    buildInfoPackage := projectPackage.value,
    buildInfoKeys ++= projectBuildInfo.value,
    // Docker
    Docker / packageName := name.value,
    Docker / packageSummary := description.value,
    Docker / maintainer := "@ColOfAbRiX",
    dockerBaseImage := "openjdk:11.0-jre",
    dockerExposedPorts ++= Seq(8001),
    dockerEntrypoint ++= Seq(
      "server.port=8001",
      "server.debug-mode=false",
    ).map { prop =>
      s"-D${organization.value}.${name.value.replace("-service", "")}.$prop"
    },
    // Dependencies
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
      TestingBundle,
    ).flatten ++ Seq(
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
    packageDescription := description.value,
    // Build Info
    buildInfoPackage := projectPackage.value,
    buildInfoKeys ++= projectBuildInfo.value,
    // Docker
    Docker / packageName := name.value,
    Docker / packageSummary := description.value,
    Docker / maintainer := "@ColOfAbRiX",
    dockerBaseImage := "openjdk:11.0-jre",
    dockerExposedPorts ++= Seq(8002),
    dockerEntrypoint ++= Seq(
      "server.port=8002",
      "server.debug-mode=false",
    ).map { prop =>
      s"-D${organization.value}.${name.value.replace("-service", "")}.$prop"
    },
    // Dependencies
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
      TestingBundle,
    ).flatten ++ Seq(
      CirceGenericDep,
      FS2CoreDep,
      ShapelessDep,
    ),
  )
