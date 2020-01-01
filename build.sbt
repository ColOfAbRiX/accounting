// General
val ScalaVersion      = "2.13.0"
val AccountingVersion = "0.1.0-SNAPSHOT"

//  - - - - - - - - - - - - - - - - - //

// Versions
val CatsScalaTestVersion = "3.0.4"
val CatsVersion          = "2.0.0"
val CirceVersion         = "0.12.3"
val FS2Version           = "2.1.0"
val Http4sVersion        = "0.21.0-M5"
val KantanCsvVersion     = "0.6.0"
val LogbackVersion       = "1.2.3"
val ScalatestVersion     = "3.1.0"
val ShapelessVersion     = "2.3.3"
// val AirframeVersion      = "19.11.0"
// val DoobieVersion        = "0.8.4"
// val MonocleVersion       = "2.0.0"
// val PureconfigVersion    = "0.12.1"
// val ScalacheckVersion    = "1.14.1"
// val TapirVersion         = "0.11.9"

// Compiler plugins
val WartRemoverVersion      = "2.4.3"
val KindProjectorVersion    = "0.10.3"
val BetterMonadicForVersion = "0.3.0"

//  - - - - - - - - - - - - - - - - - //

// Libraries
val CatsCoreDep          = "org.typelevel"    %% "cats-core"           % CatsVersion
val CatsEffectsDep       = "org.typelevel"    %% "cats-effect"         % CatsVersion
val CatsScalaTestDep     = "com.ironcorelabs" %% "cats-scalatest"      % CatsScalaTestVersion % "test"
val CirceGenericDep      = "io.circe"         %% "circe-generic"       % CirceVersion
val FS2CoreDep           = "co.fs2"           %% "fs2-core"            % FS2Version
val Http4sBlazeClientDep = "org.http4s"       %% "http4s-blaze-client" % Http4sVersion
val Http4sBlazeServerDep = "org.http4s"       %% "http4s-blaze-server" % Http4sVersion
val Http4sCirceDep       = "org.http4s"       %% "http4s-circe"        % Http4sVersion
val Http4sDslDep         = "org.http4s"       %% "http4s-dsl"          % Http4sVersion
val KantanCatsCsvDep     = "com.nrinaudo"     %% "kantan.csv-cats"     % KantanCsvVersion
val KantanCsvDep         = "com.nrinaudo"     %% "kantan.csv"          % KantanCsvVersion
val LogbackClassicDep    = "ch.qos.logback"   % "logback-classic"      % LogbackVersion
val ScalatestDep         = "org.scalatest"    %% "scalatest"           % ScalatestVersion % "test"
val ShapelessDep         = "com.chuusai"      %% "shapeless"           % ShapelessVersion
// val AirframeDep          = "org.wvlet.airframe"         %% "airframe"            % AirframeVersion
// val DoobieCoreDep        = "org.tpolecat"               %% "doobie-core"         % DoobieVersion % "test"
// val MonocleCoreDep       = "com.github.julien-truffaut" %% "monocle-core"        % MonocleVersion % "test"
// val MonocleLawDep        = "com.github.julien-truffaut" %% "monocle-law"         % MonocleVersio
// val MonocleMacroDep      = "com.github.julien-truffaut" %% "monocle-macro"       % MonocleVersion
// val PureconfigDep        = "com.github.pureconfig"      %% "pureconfig"          % PureconfigVersion
// val ScalacheckDep        = "org.scalacheck"             %% "scalacheck"          % ScalacheckVersion
// val TapirCoreDep         = "com.softwaremill.tapir"     %% "tapir-core"          % TapirVersion
// val TapirHttp4sServerDep = "com.softwaremill.tapir"     %% "tapir-http4s-server" % TapirVersion

// Compiler plugins
val BetterMonadicForPlugin = compilerPlugin("com.olegpy"      %% "better-monadic-for" % BetterMonadicForVersion)
val KindProjectorPlugin    = compilerPlugin("org.typelevel"   %% "kind-projector"     % KindProjectorVersion)
val WartremoverPlugin      = compilerPlugin("org.wartremover" %% "wartremover"        % WartRemoverVersion cross CrossVersion.full)

//  - - - - - - - - - - - - - - - - - //

// Compiler options
scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Xfatal-warnings",
)

// Wartremover
wartremoverExcluded in ThisBuild += baseDirectory.value / "src" / "test" / "scala"
wartremoverErrors in ThisBuild ++= Warts.allBut(
  // Temporary, to allow the predefined template to work
  Wart.Any,
  Wart.Nothing,
  // Wart.EitherProjectionPartial,
)

// Standardize formatting
scalafmtOnCompile in ThisBuild := true

//  - - - - - - - - - - - - - - - - - //

// Root project
lazy val accountingRoot: Project = project
  .in(file("."))
  .settings(
    organization := "com.colofabrix.scala.accounting",
    name := "accounting-root",
    version := AccountingVersion,
    scalaVersion := ScalaVersion,
    libraryDependencies ++= Seq(
      BetterMonadicForPlugin,
      KindProjectorPlugin,
      WartremoverPlugin,
    ),
  )
  .aggregate(
    accountingService
  )

// Utils project
lazy val utils = project
  .in(file("utils"))
  .settings(
    organization := "com.colofabrix.scala.accounting",
    name := "utils",
    version := AccountingVersion,
    scalaVersion := ScalaVersion,
    libraryDependencies ++= Seq(
      CatsCoreDep,
      CatsScalaTestDep,
      FS2CoreDep,
      ScalatestDep,
    ),
  )

// Service project
lazy val accountingService = project
  .in(file("accounting-service"))
  .dependsOn(
    utils
  )
  .settings(
    organization := "com.colofabrix.scala.accounting",
    name := "accounting-service",
    version := AccountingVersion,
    scalaVersion := ScalaVersion,
    libraryDependencies ++= Seq(
      CatsCoreDep,
      CatsEffectsDep,
      CatsScalaTestDep,
      CirceGenericDep,
      FS2CoreDep,
      Http4sBlazeClientDep,
      Http4sBlazeServerDep,
      Http4sCirceDep,
      Http4sDslDep,
      KantanCatsCsvDep,
      KantanCsvDep,
      LogbackClassicDep,
      ScalatestDep,
      ShapelessDep,
    ),
  )
