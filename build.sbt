import aether.AetherKeys.aetherWagons
import sbt.Keys.{parallelExecution, scalacOptions}

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

val publishSettings = Seq(
  credentials ++= sys.env
    .get("PACKAGE_CLOUD_PUBLISH_KEY")
    .map(key => Seq(Credentials("packagecloud", "packagecloud.io", "", key)))
    .getOrElse(Seq(Credentials(Path.userHome / ".ivy2" / ".credentials"))),
  aetherWagons := Seq(
    aether.WagonWrapper("packagecloud+https",
                        "io.packagecloud.maven.wagon.PackagecloudWagon")),
  publishTo := {
    Some(
      "packagecloud+https" at "packagecloud+https://packagecloud.io/500px/platform")
  }
)

lazy val root = (project in file("."))
  .settings(scalaSettings)
  .settings(name := "kinesis-stream", organization := "px")
  .settings(publishSettings)
  .settings(overridePublishSettings)
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
