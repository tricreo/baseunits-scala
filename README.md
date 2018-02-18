# Baseunits for Scala

[![Join the chat at https://gitter.im/sisioh/baseunits-scala](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/sisioh/baseunits-scala?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

This is a class library including domain models to deal with time and money.

The original product of our baseunits is Time & Money Library, Open
Source library(MIT License) developed by Eric Evans with others.
It is a very meaningful example of Domain Driven-Design.
Even more remarkable, it contributes a lot to programming Time and
Money in Java.

Unfortunately, according to Domain Language, Inc., Time & Money
Library is no longer active.

Scala version of this library

[![Build Status](https://travis-ci.org/sisioh/baseunits-scala.png?branch=develop)](https://travis-ci.org/sisioh/baseunits-scala)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.sisioh/baseunits-scala_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.sisioh/baseunits-scala_2.11)
[![Scaladoc](http://javadoc-badge.appspot.com/org.sisioh/baseunits-scala.svg?label=scaladoc)](http://javadoc-badge.appspot.com/org.sisioh/baseunits-scala_2.11)
[![Reference Status](https://www.versioneye.com/java/org.sisioh:baseunits-scala_2.11/reference_badge.svg?style=flat)](https://www.versioneye.com/java/org.sisioh:baseunits-scala_2.11/references)

## Installation

Add the following to your sbt build (Scala 2.10.x, and Scala 2.11.x, and Scala 2.12.x):

### Release Version

```scala
resolvers += "Sonatype OSS Release Repository" at "https://oss.sonatype.org/content/repositories/releases/"

libraryDependencies += "org.sisioh" %% "baseunits-scala" % "0.1.19"
```

### Snapshot Version

```scala
resolvers += "Sonatype OSS Snapshot Repository" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies += "org.sisioh" %% "baseunits-scala" % "0.1.20-SNAPSHOT"
```
