val commonSettings = Seq(
  sonatypeProfileName := "org.sisioh",
  organization := "org.sisioh",
  scalaVersion := "2.12.4",
  crossScalaVersions := Seq("2.10.7", "2.11.12", "2.12.4"),
  scalacOptions ++= {
    Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-encoding",
      "UTF-8",
      "-language:existentials",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-language:higherKinds"
    ) ++ {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2L, scalaMajor)) if scalaMajor == 12 =>
          Seq.empty
        case Some((2L, scalaMajor)) if scalaMajor <= 11 =>
          Seq(
            "-Yinline-warnings"
          )
      }
    }
  },
  resolvers ++= Seq(
    "Twitter Repository" at "http://maven.twttr.com/",
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
    "Sonatype Release Repository" at "https://oss.sonatype.org/content/repositories/releases/",
    "Sonatype Snapshot Repository" at "https://oss.sonatype.org/content/repositories/snapshots/",
    "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
    "Artima Maven Repository" at "http://repo.artima.com/releases"
  ),
  libraryDependencies ++= Seq(
    "junit"         % "junit"           % "4.8.1" % "test",
    "org.scalactic" %% "scalactic"      % "3.0.1",
    "org.scalatest" %% "scalatest"      % "3.0.1" % "test",
    "com.novocode"  % "junit-interface" % "0.8" % "test->default",
    "org.mockito"   % "mockito-core"    % "1.9.5" % "test",
    "commons-io"    % "commons-io"      % "2.4"
  ),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ =>
    false
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
  ),
  credentials := {
    val ivyCredentials = (baseDirectory in LocalRootProject).value / ".credentials"
    Credentials(ivyCredentials) :: Nil
  }
)

lazy val library = (project in file("library")).settings(
  commonSettings ++ Seq(
    name := "baseunits-scala"
  )
)

lazy val example = (project in file("example")).settings(
  commonSettings ++ Seq(
    name := "baseunits-scala-example"
  )
) dependsOn library

lazy val root = (project in file(".")).settings(
  commonSettings ++ Seq(
    name := "baseunits-scala-project"
  )
) aggregate (library, example)
