// General
val ScalaVersion      = "2.13.0"
val AccountingVersion = "0.1.0-SNAPSHOT"

// Libaries
val AirframeVersion      = "19.11.0"
val CatsScalaTestVersion = "3.0.4"
val CatsVersion          = "2.0.0"
val CirceVersion         = "0.12.3"
val DoobieVersion        = "0.8.4"
val FS2Version           = "2.1.0"
val Http4sVersion        = "0.21.0-M5"
val KantanCsvVersion     = "0.6.0"
val KittensVersion       = "2.0.0"
val LogbackVersion       = "1.2.3"
val MonixVersion         = "3.1.0"
val MonocleVersion       = "2.0.0"
val PureconfigVersion    = "0.12.1"
val ScalacheckVersion    = "1.14.1"
val ScalatestVersion     = "3.1.0"
val ShapelessVersion     = "2.3.3"
val TapirVersion         = "0.11.9"

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

// Formatting
scalafmtOnCompile := true

// Root project
lazy val accountingRoot: Project = project
  .in(file("."))
  .settings(
    organization := "com.colofabrix.scala.accounting",
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
    organization := "com.colofabrix.scala.accounting",
    name := "accounting-service",
    version := AccountingVersion,
    scalaVersion := ScalaVersion,
    libraryDependencies ++= Seq(
      "ch.qos.logback"             %  "logback-classic"     % LogbackVersion,
      "co.fs2"                     %% "fs2-core"            % FS2Version,
      "com.chuusai"                %% "shapeless"           % ShapelessVersion,
      "com.ironcorelabs"           %% "cats-scalatest"      % CatsScalaTestVersion % "test",
      "com.nrinaudo"               %% "kantan.csv-cats"     % KantanCsvVersion,
      "com.nrinaudo"               %% "kantan.csv"          % KantanCsvVersion,
      "io.circe"                   %% "circe-generic"       % CirceVersion,
      "org.http4s"                 %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s"                 %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"                 %% "http4s-circe"        % Http4sVersion,
      "org.http4s"                 %% "http4s-dsl"          % Http4sVersion,
      "org.scalatest"              %% "scalatest"           % ScalatestVersion % "test",
      "org.typelevel"              %% "cats-core"           % CatsVersion,
      "org.wvlet.airframe"         %% "airframe"            % AirframeVersion,
      // "com.github.julien-truffaut" %% "monocle-core"        % MonocleVersion,% "test",
      // "com.github.julien-truffaut" %% "monocle-law"         % MonocleVersion
      // "com.github.julien-truffaut" %% "monocle-macro"       % MonocleVersion,
      // "com.github.pureconfig"      %% "pureconfig"          % PureconfigVersion,
      // "com.softwaremill.tapir"     %% "tapir-core"          % TapirVersion,
      // "com.softwaremill.tapir"     %% "tapir-http4s-server" % TapirVersion,
      // "io.monix"                   %% "monix"               % MonixVersion,
      // "org.scalacheck"             %% "scalacheck"          % ScalacheckVersion,
      // "org.tpolecat"               %% "doobie-core"         % DoobieVersion % "test",
      // "org.typelevel"              %% "cats-effects"        % CatsVersion,
      // "org.typelevel"              %% "kittens"             % KittensVersion,
    ),
    addCompilerPlugin("com.olegpy"      %% "better-monadic-for" % BetterMonadicForVersion),
    addCompilerPlugin("org.typelevel"   %% "kind-projector"     % KindProjectorVersion),
    addCompilerPlugin("org.wartremover" %% "wartremover"        % WartRemoverVersion cross CrossVersion.full)
  )
