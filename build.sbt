name := "geohash"

version := "0.5"

scalaVersion := "2.10.3"

resolvers ++= Seq(
  "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= Seq(
  "org.scalacheck" %% "scalacheck" % "1.11.4" % "test"
)

org.scalastyle.sbt.ScalastylePlugin.Settings
