import BuildSettings.commonSettings
import org.beangle.parent.Dependencies.*

organization := "org.beangle.tools"
version := "0.1.1-SNAPSHOT"

scmInfo := Some(
  ScmInfo(
    uri("https://github.com/beangle/tools"),
    "scm:git@github.com:beangle/tools.git"
  )
)

developers := List(
  Developer(
    id = "chaostone",
    name = "Tihua Duan",
    email = "duantihua@gmail.com",
    url = uri("http://github.com/duantihua")
  )
)

description := "The Beangle Sbt Tools"
homepage := Some(uri("https://beangle.github.io/tools/index.html"))

lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    commonSettings,
    name := "sbt-beangle-tools",
    libraryDependencies ++= Seq(logback_classic, logback_core, postgresql, h2, jtds, ojdbc11, orai18n, mysql_connector_java, mssql_jdbc, HikariCP)
  )
