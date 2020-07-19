ThisBuild / scalaVersion := "2.13.3"
ThisBuild / organization := "io.github.nafg.sloth-api-module"

publish / skip := true

val githubUrl = url("https://github.com/nafg/sloth-apimodule")

ThisBuild / scmInfo := Some(
  ScmInfo(
    browseUrl = githubUrl,
    connection = "scm:git:git@github.com/nafg/sloth-apimodule.git"
  )
)
ThisBuild / homepage := Some(githubUrl)
ThisBuild / developers +=
  Developer("nafg", "Naftoli Gugenheim", "98384+nafg@users.noreply.github.com", url("https://github.com/nafg"))

lazy val core =
  crossProject(JVMPlatform, JSPlatform).crossType(CrossType.Pure)
    .settings(
      libraryDependencies ++= Seq(
        "io.circe" %%% "circe-generic" % "0.13.0",
        "io.circe" %%% "circe-parser" % "0.13.0",
        "com.github.cornerman" %%% "sloth" % "0.3.0",
        "com.propensive" %%% "magnolia" % "0.16.0",
        "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided
      )
    )
