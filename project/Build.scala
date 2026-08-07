import sbt.*
import sbt.Keys.*

object BuildSettings {
  val commonSettings = Seq(
    organizationName := "The Beangle Software",
    startYear := Some(2005),
    licenses += sbt.librarymanagement.License("GPL-3.0", uri("http://www.gnu.org/licenses/gpl-3.0.txt")),
    libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % "3.2.20" % Test),
    crossPaths := true,

    publishMavenStyle := true,
    publishConfiguration := Def.uncached(publishConfiguration.value.withOverwrite(true)),
    publishM2Configuration := Def.uncached(publishM2Configuration.value.withOverwrite(true)),
    publishLocalConfiguration := Def.uncached(publishLocalConfiguration.value.withOverwrite(true)),

    versionScheme := Some("early-semver"),
    pomIncludeRepository := { _ => false },
    sbtPluginPublishLegacyMavenStyle := false,
    publishTo := {
      val centralSnapshots = "https://central.sonatype.com/repository/maven-snapshots/"
      if version.value.endsWith("-SNAPSHOT") then Some("central-snapshots" at centralSnapshots)
      else localStaging.value
    },
    credentials += Credentials(Path.userHome / ".sbt" / "sonatype_central_credentials")
  )
}
