import sbt.Keys.*
import sbt.*

object BuildSettings {
  val commonSettings = Seq(
    organizationName := "The Beangle Software",
    startYear := Some(2005),
    licenses += ("GNU General Public License version 3", url("http://www.gnu.org/licenses/lgpl-3.0.txt")),
    libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % "3.2.19" % Test),
    crossPaths := true,

    publishMavenStyle := true,
    publishConfiguration := publishConfiguration.value.withOverwrite(true),
    publishM2Configuration := publishM2Configuration.value.withOverwrite(true),
    publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true),

    versionScheme := Some("early-semver"),
    pomIncludeRepository := { _ => false }, // Remove all additional repository other than Maven Central from POM
    sbtPluginPublishLegacyMavenStyle := false,
    publishTo := localStaging.value,
    credentials += Credentials(Path.userHome / ".sbt" / "sonatype_central_credentials")
  )

}
