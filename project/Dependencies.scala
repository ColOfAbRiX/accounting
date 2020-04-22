import sbt._

object Dependencies {

  //  LIBRARIES VERSIONS  //

  lazy val CatsVersion           = "2.1.1"
  lazy val ChimneyVersion        = "0.5.0"
  lazy val CirceVersion          = "0.13.0"
  lazy val EnumeratumCatsVersion = "1.5.16"
  lazy val EnumeratumVersion     = "1.5.15"
  lazy val FS2Version            = "2.3.0"
  lazy val Http4sVersion         = "0.21.3"
  lazy val KantanCsvVersion      = "0.6.0"
  lazy val Log4sVersion          = "1.8.2"
  lazy val LogbackVersion        = "1.2.3"
  lazy val NewtypeVersion        = "0.4.3"
  lazy val PPrintVersion         = "0.5.9"
  lazy val PureConfigVersion     = "0.12.3"
  lazy val RefinedVersion        = "0.9.13"
  lazy val ScalaTestCatsVersion  = "3.0.5"
  lazy val ScalaTestVersion      = "3.1.1"
  lazy val ShapelessVersion      = "2.3.3"
  lazy val TapirVersion          = "0.14.2"

  //  COMPILER PLUGINS VERSIONS  //

  lazy val BetterMonadicForVersion = "0.3.1"
  lazy val KindProjectorVersion    = "0.10.3"
  lazy val SplainVersion           = "0.5.1"
  lazy val WartRemoverVersion      = "2.4.5"

  //  LIBRARIES  //

  // Low-level
  lazy val CatsCoreDep          = "org.typelevel"    %% "cats-core"          % CatsVersion
  lazy val CatsEffectDep        = "org.typelevel"    %% "cats-effect"        % CatsVersion
  lazy val EnumeratumCatsDep    = "com.beachape"     %% "enumeratum-cats"    % EnumeratumCatsVersion
  lazy val EnumeratumDep        = "com.beachape"     %% "enumeratum"         % EnumeratumVersion
  lazy val NewtypeDep           = "io.estatico"      %% "newtype"            % NewtypeVersion
  lazy val RefinedCatsDep       = "eu.timepit"       %% "refined-cats"       % RefinedVersion
  lazy val RefinedDep           = "eu.timepit"       %% "refined"            % RefinedVersion
  lazy val RefinedPureconfigDep = "eu.timepit"       %% "refined-pureconfig" % RefinedVersion
  lazy val RefinedShapelessDef  = "eu.timepit"       %% "refined-shapeless"  % RefinedVersion
  lazy val ScalaTestCatsDep     = "com.ironcorelabs" %% "cats-scalatest"     % ScalaTestCatsVersion % "test"
  lazy val ScalaTestDep         = "org.scalatest"    %% "scalatest"          % ScalaTestVersion % "test"
  lazy val ShapelessDep         = "com.chuusai"      %% "shapeless"          % ShapelessVersion

  // Common utilities
  lazy val FS2CoreDep        = "co.fs2"                %% "fs2-core"       % FS2Version
  lazy val Log4sDep          = "org.log4s"             %% "log4s"          % Log4sVersion
  lazy val LogbackClassicDep = "ch.qos.logback"        % "logback-classic" % LogbackVersion
  lazy val PPrintDep         = "com.lihaoyi"           %% "pprint"         % PPrintVersion
  lazy val PureConfigDep     = "com.github.pureconfig" %% "pureconfig"     % PureConfigVersion

  // Data manipulation
  lazy val ChimneyCatsDep   = "io.scalaland" %% "chimney-cats"    % ChimneyVersion
  lazy val ChimneyDep       = "io.scalaland" %% "chimney"         % ChimneyVersion
  lazy val CirceDep         = "io.circe"     %% "circe-core"      % CirceVersion
  lazy val CirceGenericDep  = "io.circe"     %% "circe-generic"   % CirceVersion
  lazy val CirceParserDep   = "io.circe"     %% "circe-parser"    % CirceVersion
  lazy val CirceRefinedDep  = "io.circe"     %% "circe-refined"   % CirceVersion
  lazy val KantanCatsCsvDep = "com.nrinaudo" %% "kantan.csv-cats" % KantanCsvVersion
  lazy val KantanCsvDep     = "com.nrinaudo" %% "kantan.csv"      % KantanCsvVersion

  // Web service
  lazy val Http4sBlazeServerDep     = "org.http4s"                  %% "http4s-blaze-server"      % Http4sVersion
  lazy val Http4sCirceDep           = "org.http4s"                  %% "http4s-circe"             % Http4sVersion
  lazy val Http4sDslDep             = "org.http4s"                  %% "http4s-dsl"               % Http4sVersion
  lazy val TapirCatsDep             = "com.softwaremill.sttp.tapir" %% "tapir-cats"               % TapirVersion
  lazy val TapirCoreDep             = "com.softwaremill.sttp.tapir" %% "tapir-core"               % TapirVersion
  lazy val TapirHttp4sServerDep     = "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"      % TapirVersion
  lazy val TapirJsonCirceDep        = "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % TapirVersion
  lazy val TapirOpenAPICirceYamlDep = "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % TapirVersion
  lazy val TapirOpenAPIDocsDep      = "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % TapirVersion
  lazy val TapirRedocHttp4sDep      = "com.softwaremill.sttp.tapir" %% "tapir-redoc-http4s"       % TapirVersion
  lazy val TapirRefinedDep          = "com.softwaremill.sttp.tapir" %% "tapir-refined"            % TapirVersion

  //  COMPILER PLUGIN LIBRARIES  //

  lazy val BetterMonadicForPlugin = compilerPlugin("com.olegpy"    %% "better-monadic-for" % BetterMonadicForVersion)
  lazy val KindProjectorPlugin    = compilerPlugin("org.typelevel" %% "kind-projector"     % KindProjectorVersion)
  lazy val SplainPlugin           = compilerPlugin("io.tryp"       % "splain"              % SplainVersion cross CrossVersion.patch)
  lazy val WartremoverPlugin = compilerPlugin(
    "org.wartremover" %% "wartremover" % WartRemoverVersion cross CrossVersion.full,
  )

  //  DEPENDENCY BUNDLES  //

  lazy val CatsBundle       = Seq(CatsCoreDep, CatsEffectDep, ScalaTestCatsDep)
  lazy val ChimneyBundle    = Seq(ChimneyDep, ChimneyCatsDep)
  lazy val CirceBundle      = Seq(CirceDep, CirceGenericDep, CirceParserDep, CirceRefinedDep)
  lazy val ConfigBundle     = Seq(PureConfigDep, RefinedDep, RefinedPureconfigDep)
  lazy val EnumeratumBundle = Seq(EnumeratumDep, EnumeratumCatsDep)
  lazy val Http4sBundle     = Seq(Http4sBlazeServerDep, Http4sCirceDep, Http4sDslDep)
  lazy val KantanCsvBundle  = Seq(KantanCatsCsvDep, KantanCsvDep)
  lazy val LoggingBundle    = Seq(Log4sDep, LogbackClassicDep)
  lazy val RefinedBundle    = Seq(RefinedDep, RefinedCatsDep, RefinedShapelessDef)
  lazy val TapirBundle = Seq(
    TapirCatsDep,
    TapirCoreDep,
    TapirHttp4sServerDep,
    TapirJsonCirceDep,
    TapirOpenAPICirceYamlDep,
    TapirOpenAPIDocsDep,
    TapirRedocHttp4sDep,
    TapirRefinedDep,
  )

}
