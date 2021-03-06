sbtPlugin := true

organization := "jp.tricreo"

name := "baseunits-scala"

version := "0.0.1"

scalaVersion := "2.9.0-1"

libraryDependencies ++= Seq(
	"junit" % "junit" % "4.8.1" % "test",
	"org.hamcrest" % "hamcrest-library" % "1.1" % "test",
	"org.mockito" % "mockito-core" % "1.8.5" % "test",
	"org.specs" % "specs" % "1.3.1" % "test",
	"org.scalatest" % "scalatest_2.9.0" % "1.6.1",	
	"org.slf4j" % "slf4j-api" % "1.5.6",
	"org.slf4j" % "jcl-over-slf4j" % "1.5.6",
	"ch.qos.logback" % "logback-core" % "0.9.15",
	"commons-lang" % "commons-lang" % "2.5",
	"commons-io" % "commons-io" % "1.4",
	"org.sisioh" % "scala-dddbase-spec" % "0.0.1-SNAPSHOT"	
)

resolvers ++= Seq(
	"Sisioh Maven Release Repository" at "http://maven.sisioh.org/release/",
	"Sisioh Maven Snapshot Repository" at "http://maven.sisioh.org/snapshot"
)

scalacOptions += "-deprecation"

publishMavenStyle := true
