lazy val commonSettings = Seq(
  organization := "info.ditrapani",
  name := "tic-tac-toe",
  version := "0.0.1",
  scalaVersion := "2.12.4"
)

lazy val shared = crossProject
  .crossType(CrossType.Pure)
  .in(file("shared"))
  .settings(commonSettings)
lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

val Http4sVersion = "0.18.0"

lazy val server = project
  .in(file("server"))
  .settings(
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      // test
      "org.mockito" % "mockito-core" % "2.15.0" % "test",
      "org.scalatest" %% "scalatest" % "3.0.5" % "test"
    )
  )
  .dependsOn(sharedJvm)

lazy val client = project.in(file("client")).dependsOn(sharedJs)

scalacOptions ++= Seq(
  "-target:jvm-1.8",
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-unchecked",
  "-Xlint",
  "-Ypartial-unification",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused",
  "-Ywarn-value-discard",
  "-Xfuture"
)

wartremoverWarnings ++= Warts.allBut(
  Wart.Equals,
  Wart.NonUnitStatements
)

scalafmtVersion in ThisBuild := "1.4.0"
scalafmtOnCompile in ThisBuild := true
