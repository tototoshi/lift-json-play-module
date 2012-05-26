import sbt._
import sbt.Keys._

object LiftJsonPlayModuleBuild extends Build {

  lazy val liftJsonPlayModule = Project(
    id = "lift-json-play-module",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "lift-json-play-module",
      organization := "com.github.tototoshi",
      version := "0.1",
      scalaVersion := "2.9.1",
      externalResolvers ~= (_.filter(_.name != "Scala-Tools Maven2 Repository")),
      resolvers ++= Seq(
        "typesafe" at "http://repo.typesafe.com/typesafe/releases"
      ),
      libraryDependencies ++= Seq(
        "net.liftweb" %% "lift-json" % "2.4",
        "play" %% "play" % "[2,)" % "provided",
        "play" %% "play-test" % "[2,)" % "test"
      )
    )
  )
}
