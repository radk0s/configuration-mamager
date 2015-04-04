import play.Project._

name := """server"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "ws.securesocial" % "securesocial_2.10" % "2.1.4",
  "mysql" % "mysql-connector-java" % "5.1.18",
  "com.google.inject" % "guice" % "3.0"
)

playScalaSettings

