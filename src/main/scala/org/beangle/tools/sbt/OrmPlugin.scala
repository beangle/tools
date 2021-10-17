/*
 * LICENSE NEEDED!!
 */

package org.beangle.tools.sbt

import java.io.File
import sbt.Keys._
import sbt._

object OrmPlugin extends sbt.AutoPlugin {

  object autoImport {
    val ormDdl = taskKey[Unit]("Generate orm ddl files")
    val ormDdlDiff = inputKey[Unit]("Generate ddl diff")

    lazy val baseOrmSettings: Seq[Def.Setting[_]] = Seq(
      ormDdl := ormDdlTask.value,
      ormDdlDiff := {
        import complete.DefaultParsers._
        val args = spaceDelimited("<arg>").parsed
        val log = streams.value.log
        if (args.size < 2) {
          log.error("usage:ormDdlDiff oldVersion newVersion")
        } else {
          diff(baseDirectory.value, crossTarget.value, bootClasspathsTask.value,
            "PostgreSQL".toLowerCase(), args(0), args(1), log)
        }
      }
    )
  }

  import autoImport._

  override def trigger = allRequirements

  override lazy val projectSettings = inConfig(Compile)(baseOrmSettings)

  lazy val bootClasspathsTask = {
    Def.task {
      val classpaths = new collection.mutable.ArrayBuffer[Attributed[File]]
      classpaths ++= (Compile / externalDependencyClasspath).value
      classpaths ++= (Runtime / externalDependencyClasspath).value
      classpaths ++= (Compile / internalDependencyClasspath).value
      classpaths
    }
  }

  lazy val ormDdlTask =
    Def.task {
      generate(crossTarget.value.getAbsolutePath, bootClasspathsTask.value, streams.value.log)
    }

  private def generate(target: String, dependencies: collection.Seq[Attributed[File]], log: util.Logger): Unit = {
    val folder = new File(target + "/../db/")
    folder.mkdirs()
    val classpath = dependencies.map(_.data.getAbsolutePath).mkString(File.pathSeparator)
    try {
      val pb = new ProcessBuilder("java", "-cp", classpath, "org.beangle.data.orm.tool.DdlGenerator",
        "PostgreSQL,Mysql,H2,Oracle,Db2,Sqlserver", folder.getCanonicalPath, "zh_CN")
      log.debug(pb.command().toString)
      pb.inheritIO()
      val pro = pb.start()
      pro.waitFor()
      val warningFile = new File(folder.getCanonicalPath + "/warnings.txt")
      val hasWarning = warningFile.exists()
      log.info("DDl generated in " + folder.getCanonicalPath)
      if (hasWarning) {
        log.warn("Found some warnings in " + warningFile.getCanonicalPath)
      }
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

  def diff(base: File, targetBase: File, dependencies: collection.Seq[Attributed[File]], dialect: String,
           oldVersion: String, newVersion: String, log: util.Logger): Unit = {
    val folder = new File(targetBase.getAbsolutePath + "/../db/" + dialect + "/migrate")
    folder.mkdirs()
    try {
      val oldDbFile = new File(s"${base.getAbsolutePath}/src/main/resources/db/${dialect}/db-${oldVersion}.xml")
      if (!oldDbFile.exists()) {
        log.warn(s"Cannot find ${oldDbFile.getAbsolutePath}")
        return
      }
      val newDbFile = new File(s"${base.getAbsolutePath}/src/main/resources/db/${dialect}/db-${newVersion}.xml")
      if (!newDbFile.exists()) {
        log.warn(s"Cannot find ${newDbFile.getAbsolutePath}")
        return
      }
      val target = folder.getCanonicalPath + s"/${oldVersion}-${newVersion}.sql"
      val classpath = dependencies.map(_.data.getAbsolutePath).mkString(File.pathSeparator)
      val pb = new ProcessBuilder("java", "-cp", classpath.toString, "org.beangle.data.jdbc.meta.Diff",
        oldDbFile.getAbsolutePath, newDbFile.getAbsolutePath, target)
      log.debug(pb.command().toString)
      pb.inheritIO()
      val pro = pb.start()
      pro.waitFor()
      log.info("DDl diff generated in " + target)
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

}
