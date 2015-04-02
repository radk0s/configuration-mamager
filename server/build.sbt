import play.Project._

name := """server"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "ws.securesocial" % "securesocial_2.10" % "2.1.4"
)

playScalaSettings

