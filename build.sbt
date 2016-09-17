import play.Project._

name := """pay-gw"""

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.2.2", 
  "org.webjars" % "bootstrap" % "2.3.1"
)

libraryDependencies ++= Seq(
    play-jdbc,
    jdbc,
    cache,
    ws,
    "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
    "mysql" % "mysql-connector-java" % "5.1.38"
)

libraryDependencies += "com.typesafe.play" % "anorm_2.11" % "2.5.2"

playScalaSettings
