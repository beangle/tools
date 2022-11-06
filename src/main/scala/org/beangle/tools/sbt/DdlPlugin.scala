/*
 * Copyright (C) 2005, The Beangle Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful.
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.beangle.tools.sbt

import sbt.Keys._
import sbt._

import java.io.File

object DdlPlugin extends sbt.AutoPlugin {

  object autoImport {
    val ddlDiff = inputKey[Unit]("Generate ddl diff")
    val ddlReport = taskKey[Unit]("Generate ddl report")

    lazy val baseOrmSettings: Seq[Def.Setting[_]] = Seq(
      ddlReport := ddlReportTask.value,
      ddlDiff := {
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
      classpaths ++= (Runtime / fullClasspath).value
      classpaths
    }
  }
  lazy val ddlReportTask =
    Def.task {
      val reportPath = "/src/main/resources/db/postgresql/report.xml"
      val reportXML = new File(baseDirectory.value + reportPath)
      val log = streams.value.log
      resolvers.value.find(_.isInstanceOf[MavenRepository]) foreach { mc =>
        val m2Root = mc.asInstanceOf[MavenRepository].root
        if (reportXML.exists()) {
          report(m2Root, reportXML, crossTarget.value, log)
        } else {
          log.warn(s"Cannot find ${reportPath}")
        }
      }
    }

  def report(m2Root: String, xmlFile: File, target: File, log: util.Logger): Unit = {
    val rs = Dependency.resolveJar(m2Root, "org.beangle.db:beangle-db-report_3:0.0.17")
    if (rs._1) {
      val reportDir = new File(target.getAbsolutePath + "/dbreport/")
      reportDir.mkdirs()
      val targetDir = reportDir.getCanonicalPath
      val pb = new ProcessBuilder("java", "-cp", rs._2, "org.beangle.db.report.Reporter", xmlFile.getAbsolutePath, targetDir)
      log.debug(pb.command().toString)
      pb.inheritIO()
      val pro = pb.start()
      pro.waitFor()
      log.info(s"DDL report was generated in ${targetDir}")
      Tools.openBrowser(targetDir+"/index.html")
    }
  }

  def diff(base: File, targetBase: File, dependencies: collection.Seq[Attributed[File]], dialect: String,
           oldVersion: String, newVersion: String, log: util.Logger): Unit = {
    val folder = new File(targetBase.getAbsolutePath + "/db/" + dialect + "/migrate")
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
