resolvers ++= Seq(
  "Narrative Releases" at "s3://s3.amazonaws.com/narrative-artifact-releases",
  Resolver.url("sbts3 ivy resolver", url("https://dl.bintray.com/emersonloureiro/sbt-plugins"))(Resolver.ivyStylePatterns)
)

// TODO: testing out new common-build before rolling
addSbtPlugin("io.narrative" % "common-build" % "2.0.5")
addSbtPlugin("com.lucidchart" % "sbt-scalafmt" % "1.15")
