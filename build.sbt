name:= "SLP"

version := "1.0"

scalaVersion := "2.10.1"

retrieveManaged := true

defaultExcludes in unmanagedResources := "application.conf"

libraryDependencies +=  "org.scalatest" % "scalatest_2.10" % "2.0.M5b" % "test"

libraryDependencies += "com.google.code.findbugs" % "jsr305" % "1.3.9"

libraryDependencies += "com.google.guava" % "guava" % "14.0.1"

libraryDependencies += "org.joda" % "joda-convert" % "1.2"

libraryDependencies += "joda-time" % "joda-time" % "2.2"

libraryDependencies += "com.typesafe" % "config" % "1.0.1"

resolvers += "Mandubian repository snapshots" at "https://github.com/mandubian/mandubian-mvn/raw/master/snapshots/"

resolvers += "Mandubian repository releases" at "https://github.com/mandubian/mandubian-mvn/raw/master/releases/"

libraryDependencies += "play" %% "play-json" % "2.2-SNAPSHOT"

libraryDependencies += "org.specs2" %% "specs2" % "1.13" % "test"

libraryDependencies += "junit" % "junit" % "4.8" % "test"
