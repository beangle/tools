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

object Sas {
  val Engine = "org.beangle.sas" % "beangle-sas-engine" % "0.12.11" % "test"

  val TomcatCore = "org.apache.tomcat.embed" % "tomcat-embed-core" % "11.0.1" % "test" exclude("org.apache.tomcat", "tomcat-annotations-api")
  val TomcatWebSocket = "org.apache.tomcat.embed" % "tomcat-embed-websocket" % "11.0.1" % "test"
  val TomcatJasper = "org.apache.tomcat.embed" % "tomcat-embed-jasper" % "11.0.1" % "test"

  val Undertow = "io.undertow" % "undertow-servlet" % "2.3.18.Final" % "test"
}
