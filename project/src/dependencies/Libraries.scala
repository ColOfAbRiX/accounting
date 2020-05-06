package dependencies

import sbt._

/**
 * Libraries
 */
trait Libraries {

  // Versions
  lazy val CatsVersion          = "2.1.1"
  lazy val ChimneyVersion       = "0.5.1"
  lazy val CirceVersion         = "0.13.0"
  lazy val EnumeratumVersion    = "1.6.0"
  lazy val FS2Version           = "2.3.0"
  lazy val Http4sVersion        = "0.21.4"
  lazy val KantanCsvVersion     = "0.6.0"
  lazy val KittensVersion       = "2.1.0"
  lazy val Log4sVersion         = "1.8.2"
  lazy val LogbackVersion       = "1.2.3"
  lazy val NewtypeVersion       = "0.4.3"
  lazy val PPrintVersion        = "0.5.9"
  lazy val PureConfigVersion    = "0.12.3"
  lazy val RefinedVersion       = "0.9.14"
  lazy val ScalaCheckVersion    = "1.14.1"
  lazy val ScalaTestCatsVersion = "3.0.5"
  lazy val ScalaTestVersion     = "3.1.1"
  lazy val ShapelessVersion     = "2.3.3"
  lazy val SimulacrumVersion    = "1.0.0"
  lazy val TapirVersion         = "0.14.3"

  // Generic
  lazy val CatsCoreDep          = "org.typelevel"         %% "cats-core"          % CatsVersion
  lazy val CatsEffectDep        = "org.typelevel"         %% "cats-effect"        % CatsVersion
  lazy val ChimneyCatsDep       = "io.scalaland"          %% "chimney-cats"       % ChimneyVersion
  lazy val ChimneyDep           = "io.scalaland"          %% "chimney"            % ChimneyVersion
  lazy val EnumeratumCatsDep    = "com.beachape"          %% "enumeratum-cats"    % EnumeratumVersion
  lazy val EnumeratumDep        = "com.beachape"          %% "enumeratum"         % EnumeratumVersion
  lazy val FS2CoreDep           = "co.fs2"                %% "fs2-core"           % FS2Version
  lazy val KantanCatsCsvDep     = "com.nrinaudo"          %% "kantan.csv-cats"    % KantanCsvVersion
  lazy val KantanCsvDep         = "com.nrinaudo"          %% "kantan.csv"         % KantanCsvVersion
  lazy val KittensDep           = "org.typelevel"         %% "kittens"            % KittensVersion
  lazy val Log4sDep             = "org.log4s"             %% "log4s"              % Log4sVersion
  lazy val LogbackClassicDep    = "ch.qos.logback"        % "logback-classic"     % LogbackVersion
  lazy val NewtypeDep           = "io.estatico"           %% "newtype"            % NewtypeVersion
  lazy val PPrintDep            = "com.lihaoyi"           %% "pprint"             % PPrintVersion
  lazy val PureConfigDep        = "com.github.pureconfig" %% "pureconfig"         % PureConfigVersion
  lazy val RefinedCatsDep       = "eu.timepit"            %% "refined-cats"       % RefinedVersion
  lazy val RefinedDep           = "eu.timepit"            %% "refined"            % RefinedVersion
  lazy val RefinedPureconfigDep = "eu.timepit"            %% "refined-pureconfig" % RefinedVersion
  lazy val ScalaCheckDep        = "org.scalacheck"        %% "scalacheck"         % ScalaCheckVersion % "test"
  lazy val ScalaTestCatsDep     = "com.ironcorelabs"      %% "cats-scalatest"     % ScalaTestCatsVersion % Test
  lazy val ScalaTestDep         = "org.scalatest"         %% "scalatest"          % ScalaTestVersion % Test
  lazy val ShapelessDep         = "com.chuusai"           %% "shapeless"          % ShapelessVersion
  lazy val SimulacrumDep        = "org.typelevel"         %% "simulacrum"         % SimulacrumVersion

  // Circe
  lazy val CirceDep        = "io.circe" %% "circe-core"    % CirceVersion
  lazy val CirceGenericDep = "io.circe" %% "circe-generic" % CirceVersion
  lazy val CirceParserDep  = "io.circe" %% "circe-parser"  % CirceVersion
  lazy val CirceRefinedDep = "io.circe" %% "circe-refined" % CirceVersion

  // Http4s
  lazy val Http4sBlazeServerDep = "org.http4s" %% "http4s-blaze-server" % Http4sVersion
  lazy val Http4sCirceDep       = "org.http4s" %% "http4s-circe"        % Http4sVersion
  lazy val Http4sDslDep         = "org.http4s" %% "http4s-dsl"          % Http4sVersion

  // Tapir
  lazy val TapirCatsDep             = "com.softwaremill.sttp.tapir" %% "tapir-cats"               % TapirVersion
  lazy val TapirCoreDep             = "com.softwaremill.sttp.tapir" %% "tapir-core"               % TapirVersion
  lazy val TapirHttp4sServerDep     = "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"      % TapirVersion
  lazy val TapirJsonCirceDep        = "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % TapirVersion
  lazy val TapirOpenAPICirceYamlDep = "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % TapirVersion
  lazy val TapirOpenAPIDocsDep      = "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % TapirVersion
  lazy val TapirRedocHttp4sDep      = "com.softwaremill.sttp.tapir" %% "tapir-redoc-http4s"       % TapirVersion
  lazy val TapirRefinedDep          = "com.softwaremill.sttp.tapir" %% "tapir-refined"            % TapirVersion

}
