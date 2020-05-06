package dependencies

/**
 * Project dependencies
 */
object Dependencies extends Libraries with CompilerPlugins {

  lazy val ScalaLangVersion = "2.13.1"

  //  DEPENDENCY BUNDLES  //

  lazy val CatsBundle       = Seq(CatsCoreDep, CatsEffectDep, KittensDep, ScalaTestCatsDep)
  lazy val ChimneyBundle    = Seq(ChimneyDep, ChimneyCatsDep)
  lazy val CirceBundle      = Seq(CirceDep, CirceGenericDep, CirceParserDep, CirceRefinedDep)
  lazy val ConfigBundle     = Seq(PureConfigDep, RefinedDep, RefinedPureconfigDep)
  lazy val EnumeratumBundle = Seq(EnumeratumDep, EnumeratumCatsDep)
  lazy val Http4sBundle     = Seq(Http4sBlazeServerDep, Http4sCirceDep, Http4sDslDep)
  lazy val KantanCsvBundle  = Seq(KantanCatsCsvDep, KantanCsvDep)
  lazy val LoggingBundle    = Seq(Log4sDep, LogbackClassicDep)
  lazy val RefinedBundle    = Seq(RefinedDep, RefinedCatsDep)
  lazy val TestingBundle    = Seq(ScalaTestDep, ScalaCheckDep, ScalaTestCatsDep)
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
