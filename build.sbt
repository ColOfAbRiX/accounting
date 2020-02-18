import Compiler._
import Dependencies._

lazy val ProjectName      = "accounting"
lazy val ScalaLangVersion = "2.13.0"

// General
ThisBuild / organization := s"com.colofabrix.scala.${ProjectName.toLowerCase}"
ThisBuild / scalaVersion := ScalaLangVersion

// Compiler options
ThisBuild / scalacOptions ++= TpolecatOptions
ThisBuild / developers := List(
  Developer("ColOfAbRiX", "Fabrizio Colonna", "@ColOfAbRiX", url("http://github.com/ColOfAbRiX")),
)

// GIT versioning information
enablePlugins(GitVersioning)
ThisBuild / git.useGitDescribe := true
ThisBuild / git.gitTagToVersionNumber := { tag: String =>
  if (tag matches "[0-9]+\\..*") Some(tag) else None
}

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

// Settings for BuildInfo
lazy val buildInfoSettings = List(
  buildInfoPackage := organization.value,
  buildInfoKeys ++= Seq[BuildInfoKey](
    "organization"   -> organization.value,
    "description"    -> description.value,
    "projectPackage" -> {
      val subPackage = name.value.replaceAll("-service$", "").replaceAll("-", "")
      if (subPackage.nonEmpty) s"${organization.value}.$subPackage" else organization.value
    },
  )
)

// Global dependencies and compiler plugins
ThisBuild / libraryDependencies ++= Seq(
  BetterMonadicForPlugin,
  KindProjectorPlugin,
  PPrintDep,
  WartremoverPlugin,
) ++ Seq(
  LoggingBundle,
).flatten

//  PROJECTS  //

// Root project
lazy val rootProject: Project = project
  .in(file("."))
  .settings(
    name := ProjectName,
  )
  .aggregate(
    etlService,
    transactionsDbService,
  )

// Utils project
lazy val utils = project
  .in(file("accounting-utils"))
  .settings(
    name := "utils",
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
    description := "Accounting ETL Service",
    buildInfoSettings,
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
  .in(file("accounting-transactions-db-service"))
  .dependsOn(
    utils,
    model,
  )
  .enablePlugins(BuildInfoPlugin)
  .settings(
    name := "transactions-db-service",
    description := "Accounting Transactions DB Service",
    buildInfoSettings,
    libraryDependencies ++= Seq(
      HttpServiceBundle,
      KantanCsvBundle,
    ).flatten ++ Seq(
      ScalatestDep,
      ShapelessDep,
    ),
  )
