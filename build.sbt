// Libaries
val Http4sVersion = "0.20.10"
val CirceVersion = "0.11.1"
val ScalatestVersion = "3.0.8"
val LogbackVersion = "1.2.3"
val PureconfigVersion = "0.12.1"

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
  "-Ypartial-unification",
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
lazy val root = (project in file("."))
  .settings(
    organization := "com.colofabrix.scala",
    name := "accounting",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.8",
    libraryDependencies ++= Seq(
      "com.github.pureconfig" %% "pureconfig"          % PureconfigVersion,
      "org.http4s"            %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"            %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s"            %% "http4s-circe"        % Http4sVersion,
      "org.http4s"            %% "http4s-dsl"          % Http4sVersion,
      "io.circe"              %% "circe-generic"       % CirceVersion,
      "org.scalatest"         %% "scalatest"           % ScalatestVersion % "test",
      "ch.qos.logback"        %  "logback-classic"     % LogbackVersion
    ),
    addCompilerPlugin("org.typelevel"   %% "kind-projector"     % KindProjectorVersion),
    addCompilerPlugin("com.olegpy"      %% "better-monadic-for" % BetterMonadicForVersion),
    addCompilerPlugin("org.wartremover" %% "wartremover"        % WartRemoverVersion cross CrossVersion.full)
  )
