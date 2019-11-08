val ScalaVersion = "2.13.0"
val AccountingVersion = "0.1.0-SNAPSHOT"

// Libaries
val PureconfigVersion = "0.12.1"
val Http4sVersion = "0.21.0-M5"
val CirceVersion = "0.12.3"
val ScalatestVersion = "3.0.8"
val ScalacheckVersion = "1.14.1"
val LogbackVersion = "1.2.3"

// Compiler plugins
val WartRemoverVersion = "2.4.3"
val KindProjectorVersion = "0.10.3"
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
      "com.github.pureconfig" %% "pureconfig"          % PureconfigVersion,
      "org.http4s"            %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"            %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s"            %% "http4s-circe"        % Http4sVersion,
      "org.http4s"            %% "http4s-dsl"          % Http4sVersion,
      "io.circe"              %% "circe-generic"       % CirceVersion,
      "org.scalatest"         %% "scalatest"           % ScalatestVersion % "test",
      "org.scalacheck"        %% "scalacheck"          % ScalacheckVersion % "test",
      "ch.qos.logback"        %  "logback-classic"     % LogbackVersion
    ),
    addCompilerPlugin("org.typelevel"   %% "kind-projector"     % KindProjectorVersion),
    addCompilerPlugin("com.olegpy"      %% "better-monadic-for" % BetterMonadicForVersion),
    addCompilerPlugin("org.wartremover" %% "wartremover"        % WartRemoverVersion cross CrossVersion.full)
  )
