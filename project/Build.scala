import com.typesafe.sbt.SbtScalariform._
import com.typesafe.sbt.SbtSite._
import sbt.Keys._
import sbt._

import scalariform.formatter.preferences._

object BaseUnitsBuild extends Build {

  val commonSettings = scalariformSettings ++
    org.scalastyle.sbt.ScalastylePlugin.Settings ++
    ScctPlugin.instrumentSettings ++ site.settings ++ site.includeScaladoc() ++ Seq(
    organization := "org.sisioh",
    version := "0.1.14",
    scalaVersion := "2.11.1",
    crossScalaVersions := Seq("2.10.4", "2.11.1"),
    scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation"),
    resolvers ++= Seq(
      "Twitter Repository" at "http://maven.twttr.com/",
      "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
      "Sonatype Release Repository" at "https://oss.sonatype.org/content/repositories/releases/",
      "Sonatype Snapshot Repository" at "https://oss.sonatype.org/content/repositories/snapshots/"
    ),
    libraryDependencies ++= Seq(
      "junit" % "junit" % "4.8.1" % "test",
      "com.novocode" % "junit-interface" % "0.8" % "test->default",
      "org.mockito" % "mockito-core" % "1.9.5" % "test",
      "org.scalatest" %% "scalatest" % "2.1.6" % "test",
      "commons-io" % "commons-io" % "2.4"
    ),
    ScalariformKeys.preferences :=
      ScalariformKeys.preferences.value
        .setPreference(AlignParameters, true)
        .setPreference(AlignSingleLineCaseStatements, true)
        .setPreference(DoubleIndentClassDeclaration, true)
        .setPreference(PreserveDanglingCloseParenthesis, true)
        .setPreference(MultilineScaladocCommentsStartOnFirstLine, false)
    ,
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
      <url>https://github.com/sisioh/baseunits-scala</url>
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

  lazy val library = Project(
    id = "baseunits-scala-library",
    base = file("library"),
    settings = commonSettings ++ Seq(
      name := "baseunits-scala"
    )
  )

  lazy val example = Project(
    id = "baseunits-scala-example",
    base = file("example"),
    settings = commonSettings ++ Seq(
      name := "baseunits-scala-example"
    )
  ) dependsOn (library)

  lazy val root = Project(
    id = "baseunits-scala-project",
    base = file("."),
    settings = commonSettings ++ Seq(
      name := "baseunits-scala-project"
    )
  ) aggregate(library, example)

}
