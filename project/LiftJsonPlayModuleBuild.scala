import sbt._
import sbt.Keys._

object LiftJsonPlayModuleBuild extends Build {

  lazy val liftJsonPlayModule = Project(
    id = "lift-json-play-module",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "lift-json-play-module",
      organization := "com.github.tototoshi",
      version := "0.2.0",
      scalaVersion := "2.10.0",
      scalacOptions ++= Seq("-feature"),
      externalResolvers ~= (_.filter(_.name != "Scala-Tools Maven2 Repository")),
      resolvers ++= Seq(
        "typesafe" at "http://repo.typesafe.com/typesafe/releases"
      ),
      libraryDependencies ++= Seq(
        "net.liftweb" %% "lift-json" % "2.5-M4",
        "play" %% "play" % "2.1.0" % "provided",
        "play" %% "play-test" % "2.1.0" % "test"
      )
    )
  )
}
