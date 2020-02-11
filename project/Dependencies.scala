import sbt._

object Dependencies {

  //  VERSIONS  //

  lazy val CatsScalaTestVersion = "3.0.4"
  lazy val CatsVersion          = "2.1.0"
  lazy val CirceVersion         = "0.13.0-RC1"
  lazy val FS2Version           = "2.2.1"
  lazy val Http4sVersion        = "0.21.0-RC1"
  lazy val KantanCsvVersion     = "0.6.0"
  lazy val KittensVersion       = "2.0.0"
  lazy val Log4sVersion         = "1.8.2"
  lazy val LogbackVersion       = "1.2.3"
  lazy val PPrintVersion        = "0.5.8"
  lazy val PureconfigVersion    = "0.12.2"
  lazy val ScalatestVersion     = "3.1.0"
  lazy val ShapelessVersion     = "2.3.3"
  lazy val SilencerVersion      = "1.4.3"
  lazy val TapirVersion         = "0.12.16"

  //  COMPILER PLUGINS VERSIONS  //

  lazy val WartRemoverVersion      = "2.4.3"
  lazy val KindProjectorVersion    = "0.10.3"
  lazy val BetterMonadicForVersion = "0.3.0"

  //  LIBRARIES  //

  lazy val CatsCoreDep       = "org.typelevel"         %% "cats-core"       % CatsVersion
  lazy val CatsEffectsDep    = "org.typelevel"         %% "cats-effect"     % CatsVersion
  lazy val CatsScalaTestDep  = "com.ironcorelabs"      %% "cats-scalatest"  % CatsScalaTestVersion % "test"
  lazy val CirceGenericDep   = "io.circe"              %% "circe-generic"   % CirceVersion
  lazy val FS2CoreDep        = "co.fs2"                %% "fs2-core"        % FS2Version
  lazy val KantanCatsCsvDep  = "com.nrinaudo"          %% "kantan.csv-cats" % KantanCsvVersion
  lazy val KantanCsvDep      = "com.nrinaudo"          %% "kantan.csv"      % KantanCsvVersion
  lazy val KittensDep        = "org.typelevel"         %% "kittens"         % KittensVersion
  lazy val Log4sDep          = "org.log4s"             %% "log4s"           % Log4sVersion
  lazy val LogbackClassicDep = "ch.qos.logback"        % "logback-classic"  % LogbackVersion
  lazy val PPrintDep         = "com.lihaoyi"           %% "pprint"          % PPrintVersion
  lazy val PureconfigDep     = "com.github.pureconfig" %% "pureconfig"      % PureconfigVersion
  lazy val ScalatestDep      = "org.scalatest"         %% "scalatest"       % ScalatestVersion % "test"
  lazy val ShapelessDep      = "com.chuusai"           %% "shapeless"       % ShapelessVersion
  lazy val SilencerDep       = "com.github.ghik"       % "silencer-lib"     % SilencerVersion % Provided cross CrossVersion.full

  //  WEB SERVICE  //

  lazy val Http4sBlazeServerDep     = "org.http4s"                  %% "http4s-blaze-server"      % Http4sVersion
  lazy val Http4sCirceDep           = "org.http4s"                  %% "http4s-circe"             % Http4sVersion
  lazy val Http4sDslDep             = "org.http4s"                  %% "http4s-dsl"               % Http4sVersion
  lazy val TapirCoreDep             = "com.softwaremill.sttp.tapir" %% "tapir-core"               % TapirVersion
  lazy val TapirHttp4sServerDep     = "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"      % TapirVersion
  lazy val TapirJsonCirceDep        = "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % TapirVersion
  lazy val TapirOpenAPICirceYamlDep = "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % TapirVersion
  lazy val TapirOpenAPIDocsDep      = "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % TapirVersion
  lazy val TapirRedocHttp4s         = "com.softwaremill.sttp.tapir" %% "tapir-redoc-http4s"       % TapirVersion

  //  COMPILER PLUGIN LIBRARIES  //

  lazy val BetterMonadicForPlugin = compilerPlugin("com.olegpy"    %% "better-monadic-for" % BetterMonadicForVersion)
  lazy val KindProjectorPlugin    = compilerPlugin("org.typelevel" %% "kind-projector"     % KindProjectorVersion)
  lazy val WartremoverPlugin = compilerPlugin(
    "org.wartremover" %% "wartremover" % WartRemoverVersion cross CrossVersion.full,
  )
  lazy val SilencerPlugin = compilerPlugin(
    "com.github.ghik" % "silencer-plugin" % SilencerVersion cross CrossVersion.full,
  )

  //  AGGREGATED  //

  lazy val CatsBundle      = Seq(CatsCoreDep, CatsEffectsDep, CatsScalaTestDep, KittensDep)
  lazy val Http4sBundle    = Seq(Http4sBlazeServerDep, Http4sCirceDep, Http4sDslDep)
  lazy val KantanCsvBundle = Seq(KantanCatsCsvDep, KantanCsvDep)
  lazy val LoggingBundle   = Seq(Log4sDep, LogbackClassicDep)
  lazy val SilencerBundle  = Seq(SilencerDep, SilencerPlugin)
  lazy val TapirBundle = Seq(
    TapirCoreDep,
    TapirHttp4sServerDep,
    TapirJsonCirceDep,
    TapirOpenAPICirceYamlDep,
    TapirOpenAPIDocsDep,
    TapirRedocHttp4s,
  )

}
