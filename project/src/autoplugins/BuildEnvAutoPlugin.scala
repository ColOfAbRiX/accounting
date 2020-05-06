package autoplugins

import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin

/**
 * Based on https://www.scala-sbt.org/sbt-native-packager/recipes/package_configuration.html#sbt-parameters-and-build-environment
 */
object BuildEnvAutoPlugin extends AutoPlugin {
  object autoImport {
    object BuildEnv extends Enumeration {
      val Production, Staging, Test, Development = Value
    }
    val buildEnv = settingKey[BuildEnv.Value]("The current build environment")
  }
  import autoImport._

  override def trigger  = AllRequirements
  override def requires = JvmPlugin
  override def globalSettings = Seq(
    buildEnv := BuildEnv.Development,
  )
  override def projectSettings: Seq[Setting[_]] = Seq(
    buildEnv := {
      sys
        .props
        .get("env")
        .orElse(sys.env.get("ENV"))
        .map(_.trim().toLowerCase())
        .flatMap {
          case "production" => Some(BuildEnv.Production)
          case "staging"    => Some(BuildEnv.Staging)
          case "test"       => Some(BuildEnv.Test)
          case "dev"        => Some(BuildEnv.Development)
          case _            => None
        }
        .getOrElse(BuildEnv.Development)
    },
    onLoadMessage := {
      val defaultMessage = onLoadMessage.value
      val environment    = buildEnv.value
      s"""|$defaultMessage
          |Running in build environment: $environment""".stripMargin
    },
  )
}
