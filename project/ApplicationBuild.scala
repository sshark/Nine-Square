import com.github.retronym.SbtOneJar
import sbt._
import Keys._

object ApplicationBuild extends Build {

  def standardSettings = Seq(
    exportJars := true
  ) ++ Defaults.defaultSettings

//  lazy val root = Project(id = "root", base = file(".")) aggregate(common, gui, cmdline)
  lazy val root = Project(id = "root", base = file(".")) aggregate(common, gui, web)

  lazy val web = play.Project("web", path = file("web")) dependsOn(common, gui)

  lazy val common = Project(id = "common",
    base = file("common"),
    settings = standardSettings ++ Seq(libraryDependencies ++= Dependencies.common) ++ SbtOneJar.oneJarSettings)

  lazy val gui = Project(id = "gui",
    base = file("gui"),
    settings = standardSettings ++ Seq(libraryDependencies ++= Dependencies.gui) ++ SbtOneJar.oneJarSettings) dependsOn(common)

/*
  lazy val cmdline = Project(id = "cmdline",
    base = file("cmdline"),
    settings = Defaults.defaultSettings ++ Seq(libraryDependencies ++= Dependencies.cmdline)) dependsOn(common)
*/

  object Dependencies {
    object Compile {
//      val slf4jApi = "org.slf4j" % "slf4j-api" % "1.7.2"
//      val scalaz = "org.scalaz" %% "scalaz-core" % "6.0.4"
      val logback =  "ch.qos.logback" % "logback-classic" % "1.0.13"
//      val slf4s = "com.weiglewilczek.slf4s" % "slf4s_2.9.1" % "1.0.7"
      val scalaSwing = "org.scala-lang" % "scala-swing" % "2.10.1"
      val akka = "com.typesafe.akka" %% "akka-actor" % "2.2.0"

      object Test {
        val scalaTest = "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"
        val junit = "junit" % "junit" % "4.10"  % "test"
      }
    }

    import Compile._

    val testkit = Seq(Test.scalaTest, Test.junit)
//    val logback = Seq(slf4jApi, logback)
    val logging = Seq(logback)

    val common = logging ++ testkit ++ Seq(akka) // ++ Seq(scalaz)
    val gui = logging ++ testkit ++ Seq(scalaSwing) // ++ Seq(scalaz)
    val cmdline = logging ++ testkit  // ++ Seq(scalaz)
  }
}
