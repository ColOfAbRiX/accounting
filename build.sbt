import Dependencies._

lazy val ScalaLangVersion = "2.13.0"

// General
ThisBuild / organization := s"com.colofabrix.scala.accounting"
ThisBuild / scalaVersion := ScalaLangVersion

// Compiler options
ThisBuild / scalacOptions ++= Compiler.TpolecatOptions
ThisBuild / developers := List(
  Developer("ColOfAbRiX", "Fabrizio Colonna", "@ColOfAbRiX", url("http://github.com/ColOfAbRiX")),
)

// GIT version information
ThisBuild / dynverSeparator := "-"
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

// BuildInfo settings
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
    name := "root",
    description := "Accounting",
  )
  .aggregate(
    etlService,
    transactionsDbService,
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
  .in(file("module-transactions-db-service"))
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

//lazy val modules = (file("module-") * DirectoryFilter)
//  .get
//  .map { dir =>
//    Project(dir.getName, dir)
//      .enablePlugins(BuildInfoPlugin)
//      .settings(buildInfoSettings: _*)
//  }
//
//lazy val root = (project in file("."))
//  .enablePlugins(BuildInfoPlugin)
//  .settings(buildInfoSettings: _*)
//  .dependsOn(modules.map(m => m: ClasspathDependency): _*)
//  .aggregate(modules.map(m => m: ProjectReference): _*)
//  .settings(
//    name := "mysite",
//    version := "1.0"
//  )
