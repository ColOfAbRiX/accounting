package autoplugins

import src.autoplugins.AllProjectsAutoPlugin.autoImport._
import sbt._
import sbt.Keys._
import sbt.plugins._
import sbtbuildinfo.BuildInfoPlugin
import sbtbuildinfo.BuildInfoPlugin.autoImport._
import src.autoplugins.AllProjectsAutoPlugin

/**
 * Adds default settings for the BuildInfoPlugin
 */
object BuildInfoAutoPlugin extends AutoPlugin {
  object autoImport {
    val projectBuildInfo = settingKey[Seq[BuildInfoKey]]("List of BuildInfoKey shared by all projects")
  }
  import autoImport._

  override def requires = AllProjectsAutoPlugin && BuildInfoPlugin
  override def trigger  = AllRequirements
  override def projectSettings = Seq(
    buildInfoPackage := projectPackage.value,
    buildInfoKeys ++= Seq[BuildInfoKey](
      "organization"   -> organization.value,
      "description"    -> description.value,
      "projectPackage" -> projectPackage.value,
    ),
  )
}
