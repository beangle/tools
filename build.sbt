import BuildSettings.commonSettings
import org.beangle.parent.Dependencies._

ThisBuild / organization := "org.beangle.tools"
ThisBuild / version := "0.0.21-SNAPSHOT"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/beangle/tools"),
    "scm:git@github.com:beangle/tools.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id    = "chaostone",
    name  = "Tihua Duan",
    email = "duantihua@gmail.com",
    url   = url("http://github.com/duantihua")
  )
)

ThisBuild / description := "The Beangle Sbt Tools"
ThisBuild / homepage := Some(url("https://beangle.github.io/tools/index.html"))

lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    commonSettings,
    name := "sbt-beangle-tools",
    libraryDependencies ++= Seq(logback_classic, logback_core,postgresql,h2,jtds,ojdbc11,orai18n,mysql_connector_java,mssql_jdbc,HikariCP)
  )
