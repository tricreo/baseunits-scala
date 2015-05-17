import xerial.sbt.Sonatype.SonatypeKeys._
import com.typesafe.sbt.SbtScalariform._
import com.typesafe.sbt.SbtSite._
import sbt.Keys._
import sbt._

import scalariform.formatter.preferences._

object BaseUnitsBuild extends Build {

  val commonSettings = scalariformSettings ++
    org.scalastyle.sbt.ScalastylePlugin.Settings ++
    site.settings ++ site.includeScaladoc() ++ Seq(
    sonatypeProfileName := "org.sisioh",
    organization := "org.sisioh",
    scalaVersion := "2.11.6",
    crossScalaVersions := Seq("2.10.5", "2.11.6"),
    scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation"),
    resolvers ++= Seq(
      "Twitter Repository" at "http://maven.twttr.com/",
      "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
      "Sonatype Release Repository" at "https://oss.sonatype.org/content/repositories/releases/",
      "Sonatype Snapshot Repository" at "https://oss.sonatype.org/content/repositories/snapshots/",
      "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"
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
