import sbt._
import Keys._

object BaseUnitsBuild extends Build {

  lazy val root = Project(id = "baseunits-scala",
    base = file("."),
    settings = Defaults.defaultSettings ++ Seq(
      organization := "org.sisioh",
      version := "0.1.11",
      scalaVersion := "2.10.3",
      scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation"),
      resolvers ++= Seq(
        "Twitter Repository" at "http://maven.twttr.com/",
        "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
        "Sonatype Release Repository" at "https://oss.sonatype.org/content/repositories/releases/",
        "Sonatype Snapshot Repository" at "https://oss.sonatype.org/content/repositories/snapshots/"
      ),
      libraryDependencies ++= Seq(
        "junit" % "junit" % "4.8.1" % "test",
        "org.mockito" % "mockito-core" % "1.9.5" % "test",
        "org.scalatest" %% "scalatest" % "1.9.1" % "test",
        "commons-io" % "commons-io" % "2.4",
        "org.sisioh" %% "scala-dddbase-spec" % "0.1.25",
        "org.sisioh" %% "scala-toolbox" % "0.0.7"
      ),
      publishMavenStyle := true,
      publishArtifact in Test := false,
      pomIncludeRepository := {
        _ => false
      },
      publishTo <<= version {
        (v: String) =>
          val nexus = "https://oss.sonatype.org/"
          if (v.trim.endsWith("SNAPSHOT"))
            Some("snapshots" at nexus + "content/repositories/snapshots")
          else
            Some("releases" at nexus + "service/local/staging/deploy/maven2")
      },
      pomExtra := (
        <url>https://github.com/sisioh/sisioh-dddbase</url>
          <licenses>
            <license>
              <name>Apache License Version 2.0</name>
              <url>http://www.apache.org/licenses/</url>
              <distribution>repo</distribution>
            </license>
          </licenses>
          <scm>
            <url>git@github.com:sisioh/baseunits-scala.git</url>
            <connection>scm:git:git@github.com:sisioh/baseunits-scala.git</connection>
          </scm>
          <developers>
            <developer>
              <id>j5ik2o</id>
              <name>Junichi Kato</name>
              <url>http://j5ik2o.me</url>
            </developer>
          </developers>
        )
    )
  )

}
