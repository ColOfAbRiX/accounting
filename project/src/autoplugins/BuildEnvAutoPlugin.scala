package autoplugins

import autoplugins.BuildEnvAutoPlugin.autoImport
import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin
import sbtbuildinfo.BuildInfoPlugin
import sbtbuildinfo.BuildInfoPlugin.autoImport._

/**
 * Based on https://www.scala-sbt.org/sbt-native-packager/recipes/package_configuration.html#sbt-parameters-and-build-environment
 */
object BuildEnvAutoPlugin extends AutoPlugin {
  object autoImport {
    object BuildEnv extends Enumeration {
      val Production, Staging, Test, Development = Value
    }
    val buildEnv = settingKey[BuildEnv.Value]("The build environment")
  }
  import autoImport._

  override def requires: Plugins      = JvmPlugin && BuildInfoPlugin
  override def trigger: PluginTrigger = AllRequirements
  override def globalSettings: Seq[Setting[_]] = Seq(
    buildEnv := environment,
  )
  override def projectSettings: Seq[Setting[_]] = Seq(
    buildEnv := environment,
    buildInfoKeys ++= Seq[BuildInfoKey](
      "environment" -> environment,
    ),
    onLoadMessage := {
      val defaultMessage = onLoadMessage.value
      s"""|$defaultMessage
          |Running in build environment: ${buildEnv.value}""".stripMargin
    },
  )

  private def environment: BuildEnv.Value =
    sys
      .props
      .get("environment")
      .orElse(sys.env.get("ENVIRONMENT"))
      .map(_.trim().toLowerCase())
      .flatMap {
        case "production"  => Some(BuildEnv.Production)
        case "staging"     => Some(BuildEnv.Staging)
        case "test"        => Some(BuildEnv.Test)
        case "development" => Some(BuildEnv.Development)
        case _             => None
      }
      .getOrElse(BuildEnv.Development)
}
