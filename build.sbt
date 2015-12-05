name := "Nine Square"

scalaVersion in ThisBuild := "2.11.7"

lazy val commonSettings = Seq(
  organization := "com.teckhooi",
  version := "1.0-Beta"
)

lazy val root = project in file(".") aggregate(common, web)

lazy val web = project
  .settings(commonSettings)
  .enablePlugins(PlayScala)
  .dependsOn(common)
  .configs(IntegrationTest)

lazy val common = project
  .settings(commonSettings)

libraryDependencies in ThisBuild ++=  Seq(
  "org.clapper" %% "grizzled-slf4j" % "1.0.2",
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.13",
  "net.codingwell" %% "scala-guice" % "4.0.1"
)

libraryDependencies in common ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.1.0",
  "com.typesafe.akka" %% "akka-actor" % "2.3.13",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "junit" % "junit" % "4.12"
)

libraryDependencies in web ++= Seq(
  cache,
  specs2 % Test,
  evolutions,
  filters,
  "com.mohiva" %% "play-silhouette" % "3.0.0",
  "com.mohiva" %% "play-silhouette-testkit" % "3.0.0" % "test",
  "org.scalatestplus" %% "play" % "1.4.0-M4" % "test",
  "be.objectify" %% "deadbolt-scala" % "2.4.1",
  "org.webjars" %% "webjars-play" % "2.4.0",
  "net.ceedubs" %% "ficus" % "1.1.2",
  "com.typesafe.play" %% "play-slick" % "1.1.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.1.1",
  "com.h2database" % "h2" % "1.4.187",
  "mysql" % "mysql-connector-java" % "5.1.36"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

resolvers += "Atlassian Releases" at "https://maven.atlassian.com/public/"

resolvers += Resolver.url("Typesafe Ivy releases", url("https://repo.typesafe.com/typesafe/ivy-releases"))(Resolver.ivyStylePatterns)

resolvers += Resolver.sonatypeRepo("snapshots")

/*
 * Enable fork in Test or run for javaOptions to take effect. Otherwise,
 * -Dconfig.file and -Dconfig.resource will not be enforced
 */
fork in run := false

fork in Test := true

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xlint", // Enable recommended additional warnings.
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
  "-Ywarn-numeric-widen" // Warn when numerics are widened.
)

scalacOptions in (Compile, doc) ++= Seq(
  "-no-link-warnings" // Suppresses problems with Scaladoc @throws links
)

javaOptions in Test ++= Seq("-Dconfig.resource=test-application.conf")

Defaults.itSettings

val funTestFilter: String => Boolean = {
  name => (name endsWith "ItSpec") || (name endsWith "IntegrationSpec")
}

testOptions in IntegrationTest += Tests.Filter(funTestFilter)
