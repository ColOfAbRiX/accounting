package autoplugins

import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import com.typesafe.sbt.packager.docker.DockerPlugin
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._
import com.typesafe.sbt.SbtNativePackager.autoImport._
import sbt._
import sbt.Keys._

/**
 * Adds default settings for the DockerPlugin
 */
object DockerAutoPlugin extends AutoPlugin {
  object autoImport {
    val dockerJavaProperties = settingKey[Seq[String]]("Java properties to be provided as command line overrides")
  }
  import autoImport._

  override def requires = DockerPlugin && JavaAppPackaging
  override def trigger  = AllRequirements
  override def globalSettings = Seq(
    dockerJavaProperties := Seq(),
  )
  override def projectSettings = Seq(
    dockerBaseImage := "openjdk:11.0-jre",
    dockerEntrypoint ++= dockerJavaProperties
      .value
      .map { property =>
        val projectName = name.value.replaceAll("-service$", "").replaceAll("-", "")
        s"-D${organization.value}.$projectName.$property"
      },
    maintainer := developers.value.headOption.map(_.email).getOrElse(""),
    packageDescription := description.value,
    packageName := name.value,
    packageSummary := description.value,
  )
}
