import sbt.Keys.{parallelExecution, scalacOptions}
import _root_.io.narrative.build._
import _root_.io.narrative.build.LibraryProjectSettings

lazy val ScalaVersion = "2.11.12"
val scalaSettings = Seq(
  scalaVersion := ScalaVersion,
  scalacOptions ++= Seq(
    "-deprecation", // Emit warning and location for usages of deprecated APIs.
    "-feature", // Emit warning and location for usages of features that should be imported explicitly.
    "-target:jvm-1.8",
    "-Ywarn-dead-code" // Warn when dead code is identified.
  )
)
val akkaStreamV = "2.5.19"

val dependencySettings = Seq(
  libraryDependencies ++= Seq(
    "software.amazon.kinesis" % "amazon-kinesis-client" % "2.0.4",
    "com.typesafe.akka" %% "akka-stream" % akkaStreamV,
    "com.typesafe.akka" %% "akka-slf4j" % akkaStreamV,
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
    "org.codehaus.groovy" % "groovy-all" % "2.4.1",
    "org.scalamock" %% "scalamock" % "4.1.0" % Test,
    "org.scalatest" %% "scalatest" % "3.0.5" % Test,
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaStreamV % Test
  )
)

val NarrativeReleases = "Narrative Releases" at "s3://s3-us-east-1.amazonaws.com/narrative-artifact-releases"
val NarrativeSnapshots = "Narrative Snapshots" at "s3://s3-us-east-1.amazonaws.com/narrative-artifact-snapshots"

lazy val SupportedScalaVersions = Seq(ScalaVersion)

lazy val PublishSettings = Seq(
  publishMavenStyle := false,
  publishArtifact := true,
  publishArtifact in (Test, packageBin) := true,
  publishArtifact in (Compile, packageDoc) := false,
  packagedArtifacts += ((artifact in makePom).value, makePom.value),
  publishTo := {
    if (version.value.contains("SNAPSHOT")) Some(NarrativeSnapshots)
    else Some(NarrativeReleases)
  },
  pomIncludeRepository := { _ =>
    true
  }
)

lazy val root = (project in file("."))
  .settings(scalaSettings)
  .settings(name := "kinesis-stream", organization := "px")
  .settings(PublishSettings)
  .settings(dependencySettings)
  .settings(
    parallelExecution in Test := false,
    logBuffered in Test := false,
    scalafmtOnCompile in ThisBuild := true, // all projects
    scalafmtOnCompile := true, // current project
    scalafmtOnCompile in Compile := true
  )

// examples
lazy val examples = (project in file("examples"))
  .dependsOn(root)
  .settings(scalaSettings)
  .settings(dependencySettings)
  .settings(
    resolvers += Resolver.bintrayRepo("streetcontxt", "maven"),
    libraryDependencies += "com.streetcontxt" %% "kpl-scala" % "1.0.5"
  )
