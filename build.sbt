// General
val ScalaVersion      = "2.13.0"
val AccountingVersion = "0.1.0-SNAPSHOT"

// Libaries
val AirframeVersion   = "19.11.0"
val CirceVersion      = "0.12.3"
val Http4sVersion     = "0.21.0-M5"
val LogbackVersion    = "1.2.3"
val MonocleVersion    = "2.0.0"
val PureconfigVersion = "0.12.1"
val ScalacheckVersion = "1.14.1"
val ScalatestVersion  = "3.0.8"
val KantanCsvVersion  = "0.6.0"
// val DoobieVersion    = "0.8.4"
// val MonixVersion     = "3.1.0"
// val TapirVersion     = "0.11.9"

// Compiler plugins
val WartRemoverVersion      = "2.4.3"
val KindProjectorVersion    = "0.10.3"
val BetterMonadicForVersion = "0.3.0"

// Compiler options
scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Xfatal-warnings",
)

// Wartremover
wartremoverExcluded += baseDirectory.value / "src" / "test" / "scala"
wartremoverErrors ++= Warts.allBut(
  // Temporary, to allow the predefined template to work
  Wart.Any,
  Wart.Nothing,
  Wart.EitherProjectionPartial
)

// Root project
lazy val accountingRoot: Project = project
  .in(file("."))
  .settings(
    organization := "com.colofabrix.scala",
    name := "accounting-root",
    version := AccountingVersion,
    scalaVersion := ScalaVersion
  )
  .aggregate(
    accountingService
  )

// Root project
lazy val accountingService = project
  .in(file("accounting-service"))
  .settings(
    organization := "com.colofabrix.scala",
    name := "accounting-service",
    version := AccountingVersion,
    scalaVersion := ScalaVersion,
    libraryDependencies ++= Seq(
      "ch.qos.logback"             %  "logback-classic"     % LogbackVersion,
      "com.github.pureconfig"      %% "pureconfig"          % PureconfigVersion,
      "com.nrinaudo"               %% "kantan.csv"          % KantanCsvVersion,
      "com.nrinaudo"               %% "kantan.csv-cats"     % KantanCsvVersion,
      "com.nrinaudo"               %% "kantan.csv-generic"  % KantanCsvVersion,
      "com.nrinaudo"               %% "kantan.csv-java8"    % KantanCsvVersion,
      "io.circe"                   %% "circe-generic"       % CirceVersion,
      "org.http4s"                 %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s"                 %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"                 %% "http4s-circe"        % Http4sVersion,
      "org.http4s"                 %% "http4s-dsl"          % Http4sVersion,
      "org.scalacheck"             %% "scalacheck"          % ScalacheckVersion % "test",
      "org.scalatest"              %% "scalatest"           % ScalatestVersion % "test",
      "org.wvlet.airframe"         %% "airframe"            % AirframeVersion
      // "com.github.julien-truffaut" %% "monocle-core"        % MonocleVersion,
      // "com.github.julien-truffaut" %% "monocle-macro"       % MonocleVersion,
      // "com.github.julien-truffaut" %%  "monocle-law"        % MonocleVersion % "test",
      // "com.softwaremill.tapir"     %% "tapir-core"          % TapirVersion,
      // "com.softwaremill.tapir"     %% "tapir-http4s-server" % TapirVersion
      // "io.monix"                   %% "monix"               % MonixVersion,
      // "org.tpolecat"               %% "doobie-core"         % DoobieVersion,
    ),
    addCompilerPlugin("com.olegpy"      %% "better-monadic-for" % BetterMonadicForVersion),
    addCompilerPlugin("org.typelevel"   %% "kind-projector"     % KindProjectorVersion),
    addCompilerPlugin("org.wartremover" %% "wartremover"        % WartRemoverVersion cross CrossVersion.full)
  )
