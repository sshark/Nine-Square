name := "Nine Square"

version := "1.0-Beta"

scalaVersion in ThisBuild := "2.11.6"

// Add Typesafe repository to common resolvers. Otherwise, it will only refer to
// Maven repository because it is a non-Play project module. Use sbt 'resolvers'
// to verify sub modules repositories.
resolvers in common += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
