package src.autoplugins

import sbt.Keys._
import sbt.librarymanagement.ModuleID
import sbt._

/**
 * Adds default settings for the BuildInfoPlugin
 * For some instructions see:
 *   https://amirkarimi.me/2017/05/17/how-to-apply-settings-to-multiple-projects-using-sbt-triggered-plugins.html
 */
object AllProjectsAutoPlugin extends AutoPlugin {
  object autoImport {
    implicit def singleBundled(dep: ModuleID): Seq[ModuleID] = Seq(dep)

    val projectPackage = settingKey[String]("The root package where a project resides")
    val bundledDependencies = settingKey[Seq[Seq[ModuleID]]]("Library dependencies bundled together")
  }
  import autoImport._

  override def trigger = AllRequirements
  override def projectSettings = Seq(
    bundledDependencies := Seq(),
    libraryDependencies ++= bundledDependencies.value.flatten,
    projectPackage := organization.value + "." + name.value.replaceAll("-service$", "").replaceAll("-", ""),
  )
}
