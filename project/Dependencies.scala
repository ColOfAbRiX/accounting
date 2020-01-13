import sbt._

object Dependencies {

  // Versions
  lazy val CatsScalaTestVersion = "3.0.4"
  lazy val CatsVersion          = "2.0.0"
  lazy val CirceVersion         = "0.12.3"
  lazy val FS2Version           = "2.1.0"
  lazy val Http4sVersion        = "0.21.0-M5"
  lazy val KantanCsvVersion     = "0.6.0"
  lazy val KittensVersion       = "2.0.0"
  lazy val LogbackVersion       = "1.2.3"
  lazy val ScalatestVersion     = "3.1.0"
  lazy val ShapelessVersion     = "2.3.3"
  lazy val SilencerVersion      = "1.4.3"
  lazy val TapirVersion         = "0.12.0"
  // val AirframeVersion      = "19.11.0"
  // val DoobieVersion        = "0.8.4"
  // val MonocleVersion       = "2.0.0"
  // val PureconfigVersion    = "0.12.1"
  // val ScalacheckVersion    = "1.14.1"

  // Compiler plugins
  lazy val WartRemoverVersion      = "2.4.3"
  lazy val KindProjectorVersion    = "0.10.3"
  lazy val BetterMonadicForVersion = "0.3.0"

  //  - - - - - - - - - - - - - - - - - //

  // Libraries
  lazy val CatsCoreDep          = "org.typelevel"               %% "cats-core"           % CatsVersion
  lazy val CatsEffectsDep       = "org.typelevel"               %% "cats-effect"         % CatsVersion
  lazy val CatsScalaTestDep     = "com.ironcorelabs"            %% "cats-scalatest"      % CatsScalaTestVersion % "test"
  lazy val CirceGenericDep      = "io.circe"                    %% "circe-generic"       % CirceVersion
  lazy val FS2CoreDep           = "co.fs2"                      %% "fs2-core"            % FS2Version
  lazy val Http4sBlazeServerDep = "org.http4s"                  %% "http4s-blaze-server" % Http4sVersion
  lazy val Http4sCirceDep       = "org.http4s"                  %% "http4s-circe"        % Http4sVersion
  lazy val Http4sDslDep         = "org.http4s"                  %% "http4s-dsl"          % Http4sVersion
  lazy val KantanCatsCsvDep     = "com.nrinaudo"                %% "kantan.csv-cats"     % KantanCsvVersion
  lazy val KantanCsvDep         = "com.nrinaudo"                %% "kantan.csv"          % KantanCsvVersion
  lazy val KittensDep           = "org.typelevel"               %% "kittens"             % KittensVersion
  lazy val LogbackClassicDep    = "ch.qos.logback"              %  "logback-classic"     % LogbackVersion
  lazy val ScalatestDep         = "org.scalatest"               %% "scalatest"           % ScalatestVersion % "test"
  lazy val ShapelessDep         = "com.chuusai"                 %% "shapeless"           % ShapelessVersion
  lazy val SilencerDep          = "com.github.ghik"             %  "silencer-lib"        % SilencerVersion % Provided cross CrossVersion.full
  lazy val TapirCoreDep         = "com.softwaremill.sttp.tapir" %% "tapir-core"          % TapirVersion
  lazy val TapirHttp4sServerDep = "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % TapirVersion
  lazy val TapirJsonCirceDep    = "com.softwaremill.sttp.tapir" %% "tapir-json-circe"    % TapirVersion
  // val AirframeDep          = "org.wvlet.airframe"         %% "airframe"            % AirframeVersion
  // val DoobieCoreDep        = "org.tpolecat"               %% "doobie-core"         % DoobieVersion % "test"
  // val Http4sBlazeClientDep = "org.http4s"                 %% "http4s-blaze-client" % Http4sVersion
  // val MonocleCoreDep       = "com.github.julien-truffaut" %% "monocle-core"        % MonocleVersion % "test"
  // val MonocleLawDep        = "com.github.julien-truffaut" %% "monocle-law"         % MonocleVersion
  // val MonocleMacroDep      = "com.github.julien-truffaut" %% "monocle-macro"       % MonocleVersion
  // val PureconfigDep        = "com.github.pureconfig"      %% "pureconfig"          % PureconfigVersion
  // val ScalacheckDep        = "org.scalacheck"             %% "scalacheck"          % ScalacheckVersion

  // Compiler plugins
  lazy val BetterMonadicForPlugin = compilerPlugin("com.olegpy"      %% "better-monadic-for" % BetterMonadicForVersion)
  lazy val KindProjectorPlugin    = compilerPlugin("org.typelevel"   %% "kind-projector"     % KindProjectorVersion)
  lazy val WartremoverPlugin      = compilerPlugin("org.wartremover" %% "wartremover"        % WartRemoverVersion cross CrossVersion.full)
  lazy val SilencerPlugin         = compilerPlugin("com.github.ghik" %  "silencer-plugin"    % SilencerVersion cross CrossVersion.full)

}
