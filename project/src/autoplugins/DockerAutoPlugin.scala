package autoplugins

import com.typesafe.sbt.SbtNativePackager.autoImport._
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import com.typesafe.sbt.packager.docker.DockerPlugin
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._
import sbt.Keys._
import sbt._

/**
 * Adds default settings for the DockerPlugin
 */
object DockerAutoPlugin extends AutoPlugin {
  object autoImport {
    val dockerJavaProperties = settingKey[Seq[String]]("Properties to be provided as command line overrides")
  }
  import autoImport._

  override def requires = DockerPlugin && JavaAppPackaging
  override def trigger  = AllRequirements
  override def projectSettings = Seq(
    dockerBaseImage := "openjdk:11.0-jre",
    dockerEntrypoint ++= dockerJavaProperties
      .value
      .map { prop =>
        val projectName = name.value.replaceAll("-service$", "").replaceAll("-", "")
        s"-D${organization.value}.$projectName.$prop"
      },
    dockerJavaProperties := Seq(),
    maintainer := developers.value.headOption.map(_.email).getOrElse(""),
    packageDescription := description.value,
    packageName := name.value,
    packageSummary := description.value,
  )
}
