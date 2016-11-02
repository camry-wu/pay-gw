import play.Project._

name := """pay-gw"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.8"

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.2.2", 
  "org.webjars" % "bootstrap" % "2.3.1"
)

// libraryDependencies += evolutions

libraryDependencies ++= Seq(
    jdbc,
    cache,
    // ws,
    // "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
    "com.typesafe.play" % "anorm_2.10" % "2.5.0",
    "joda-time" % "joda-time" % "2.3",
    "mysql" % "mysql-connector-java" % "5.1.38"
)

scalacOptions ++= Seq(
  "-encoding",
  "UTF-8",
  "-deprecation",
  "-unchecked",
  "-feature",
  "-language:postfixOps",
  "-language:implicitConversions"
)

playScalaSettings

// lazy val root = (project in file(".")).enablePlugins(PlayScala)
