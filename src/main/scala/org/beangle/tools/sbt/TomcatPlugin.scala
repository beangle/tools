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

import sbt.*
import sbt.Keys.*

import java.io.File
import scala.collection.mutable

object TomcatPlugin extends sbt.AutoPlugin {

  object autoImport {
    val tomcatStart = inputKey[Unit]("start tomcat server")

    lazy val baseSettings: Seq[Setting[_]] = Seq(
      libraryDependencies ++= Seq(Sas.Engine, Sas.TomcatCore, Sas.TomcatWebSocket, Sas.JulToSlf4j),
      Compile / mainClass := Some("org.beangle.sas.engine.tomcat.Bootstrap"),
      tomcatStart := {
        import complete.DefaultParsers.*
        val args = spaceDelimited("<arg>").parsed
        launchTomcat(crossTarget.value.getAbsolutePath, bootClasspathsTask.value, args, streams.value.log)
      }
    )
  }

  lazy val bootClasspathsTask = {
    Def.task {
      val classpaths = new collection.mutable.ArrayBuffer[Attributed[File]]
      classpaths ++= (Test / fullClasspath).value
      val testClasses = classpaths.find(x => x.data.isDirectory && x.data.getName == "test-classes")
      classpaths --= testClasses
      classpaths
    }
  }

  import autoImport.*

  override lazy val projectSettings: Seq[Setting[_]] = baseSettings

  private def launchTomcat(target: String, dependencies: collection.Seq[Attributed[File]], args: Seq[String], log: util.Logger): Unit = {
    val folder = new File(target + "/tomcat/")
    folder.mkdirs()
    val classpath = dependencies.map(_.data.getAbsolutePath).mkString(File.pathSeparator)
    try {
      val cmds = new mutable.ArrayBuffer[String]
      cmds += ("java")
      cmds += ("-cp")
      cmds += (classpath)
      //cmds += "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5555"
      cmds += ("org.beangle.sas.engine.tomcat.Bootstrap")
      cmds += "--dev=true"
      cmds ++= args
      import scala.jdk.javaapi.CollectionConverters.asJava
      val pb = new ProcessBuilder(asJava(cmds))
      pb.inheritIO()
      val pro = pb.start()
      pro.waitFor()
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }
}
