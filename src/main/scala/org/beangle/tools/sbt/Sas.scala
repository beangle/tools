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
  val Engine = "org.beangle.sas" % "beangle-sas-engine" % "0.12.6" % "test"
  val Tomcat = "org.apache.tomcat.embed" % "tomcat-embed-core" % "10.1.30" % "test" exclude("org.apache.tomcat", "tomcat-annotations-api")
  val Undertow = "io.undertow" % "undertow-servlet" % "2.3.17.Final" % "test"
}
