//  VERSIONS  //

lazy val AssemblyVersion       = "0.14.6"
lazy val BuildinfoVesion       = "0.9.0"
lazy val ErrorsSummaryVersion  = "0.6.3"
lazy val GitVersion            = "1.0.0"
lazy val NativePackagerVersion = "1.3.25"
lazy val RevolverVersion       = "0.9.1"
lazy val SbtStatsVersion       = "1.0.7"
lazy val ScalafixVersion       = "0.9.11"
lazy val ScalafmtVersion       = "2.3.0"
lazy val WartremoverVersion    = "2.4.3"

//  PLUGIN LIBRARIES  //

addSbtPlugin("ch.epfl.scala"    % "sbt-scalafix"        % ScalafixVersion)
addSbtPlugin("com.eed3si9n"     % "sbt-assembly"        % AssemblyVersion)
addSbtPlugin("com.eed3si9n"     % "sbt-buildinfo"       % BuildinfoVesion)
addSbtPlugin("com.orrsella"     % "sbt-stats"           % SbtStatsVersion)
addSbtPlugin("com.typesafe.sbt" % "sbt-git"             % GitVersion)
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % NativePackagerVersion)
addSbtPlugin("io.spray"         % "sbt-revolver"        % RevolverVersion)
addSbtPlugin("org.duhemm"       % "sbt-errors-summary"  % ErrorsSummaryVersion)
addSbtPlugin("org.scalameta"    % "sbt-scalafmt"        % ScalafmtVersion)
addSbtPlugin("org.wartremover"  % "sbt-wartremover"     % WartremoverVersion)
