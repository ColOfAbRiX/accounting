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

// Discpver projects from directories
lazy val modules: Seq[Project] = (file(".") * "module-*" filter (_.isDirectory))
  .get
  .map { dir =>
    val projectName = dir.name.replaceAll("^module-", "")
    val projectNameCamel = """-([a-z\d])""".r.replaceAllIn(projectName, {m =>
      m.group(1).toUpperCase()
    })
    Project(projectNameCamel, dir)
      .enablePlugins(BuildInfoPlugin)
      .settings(
        name := dir.name,
        buildInfoSettings
      )
  }

// Root project
lazy val rootProject = project
  .in(file("."))
  .settings(
    name := "accounting",
    description := "Accounting",
  )
  .aggregate(modules.map(m => m: ProjectReference): _*)
  .dependsOn(modules.map(m => m: ClasspathDependency): _*)

//// Root project
//lazy val rootProject: Project = project
//  .in(file("."))
//  .settings(
//    name := "accounting",
//    description := "Accounting",
//  )
//  .aggregate(
//    etlService,
//    transactionsDbService,
//  )


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
    libraryDependencies ++= Seq(
      HttpServiceBundle,
      KantanCsvBundle,
    ).flatten ++ Seq(
      ScalatestDep,
      ShapelessDep,
    ),
  )
