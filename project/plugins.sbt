val AssemblyVersion       = "0.14.6"
val NativePackagerVersion = "1.3.25"
val RevolverVersion       = "0.9.1"
val SbtStatsVersion       = "1.0.7"
val ScalafixVersion       = "0.9.11"
val ScalafmtVersion       = "2.3.0"
val WartremoverVersion    = "2.4.3"

addSbtPlugin("ch.epfl.scala"    % "sbt-scalafix"        % ScalafixVersion)
addSbtPlugin("com.eed3si9n"     % "sbt-assembly"        % AssemblyVersion)
addSbtPlugin("com.orrsella"     % "sbt-stats"           % SbtStatsVersion)
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % NativePackagerVersion)
addSbtPlugin("io.spray"         % "sbt-revolver"        % RevolverVersion)
addSbtPlugin("org.scalameta"    % "sbt-scalafmt"        % ScalafmtVersion)
addSbtPlugin("org.wartremover"  % "sbt-wartremover"     % WartremoverVersion)
