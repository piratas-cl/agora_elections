// This file is part of agora_elections.
// Copyright (C) 2014-2016  Agora Voting SL <agora@agoravoting.com>

// agora_elections is free software: you can redistribute it and/or modify
// it under the terms of the GNU Affero General Public License as published by
// the Free Software Foundation, either version 3 of the License.

// agora_elections  is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Affero General Public License for more details.

// You should have received a copy of the GNU Affero General Public License
// along with agora_elections.  If not, see <http://www.gnu.org/licenses/>.

name := """agora-elections"""

version := "1.0-SNAPSHOT"

fork in run := true

fork in Test := true

javaOptions in Test += "-Dconfig.file=conf/application.test.conf"

trapExit in run := false

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "com.typesafe.play" %% "play-slick" % "1.1.1",
  "org.postgresql" % "postgresql" % "9.4.1212",
  "org.bouncycastle" % "bcprov-jdk16" % "1.46",
  "com.googlecode.owasp-java-html-sanitizer" % "owasp-java-html-sanitizer" % "20170329.1",
  "commons-validator" % "commons-validator" % "1.6",
  "com.github.mumoshu" %% "play2-memcached-play24" % "0.7.0",
  "com.chuusai" %% "shapeless" % "2.0.0"
)

resolvers += "Spy Repository" at "http://files.couchbase.com/maven2" // required to resolve `spymemcached`, the plugin's dependency.