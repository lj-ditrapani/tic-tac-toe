import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

lazy val commonSettings = Seq(
  organization := "info.ditrapani",
  version := "1.0.2",
  scalaVersion := "2.12.6",
  scalacOptions ++= Seq(
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
  ),
  libraryDependencies ++= Seq(
    // test
    "org.mockito" % "mockito-core" % "2.18.3" % "test",
    "org.scalatest" %% "scalatest" % "3.0.5" % "test"
  ),
  wartremoverWarnings ++= Warts.allBut(
    Wart.Equals,
    Wart.NonUnitStatements,
    Wart.ToString
  )
)

lazy val shared = crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure).in(file("shared")).settings(commonSettings)
lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

val Http4sVersion = "0.18.12"

lazy val server = project
  .in(file("server"))
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.3"
    )
  )
  .dependsOn(sharedJvm)

lazy val client = project
  .in(file("client"))
  .settings(
    commonSettings,
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "be.doeraene" %%% "scalajs-jquery" % "0.9.3",
      "com.lihaoyi" %%% "scalatags" % "0.6.7",
      "fr.hmil" %%% "roshttp" % "2.1.0",
      "org.scala-js" %%% "scalajs-dom" % "0.9.6"
    ),
    skip in packageJSDependencies := false,
    jsDependencies += "org.webjars" % "jquery" % "3.3.1" / "3.3.1/jquery.js",
    jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()
  )
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(sharedJs)

sharedJvm / scalafmtOnCompile := true
sharedJs / scalafmtOnCompile := true
server / scalafmtOnCompile := true
client / scalafmtOnCompile := true

lazy val copyJs = taskKey[Unit]("Copies client Javascript files to server resources.")
copyJs := {
  val log = streams.value.log
  log.info("Copying client Javascript files to server resources.")
  import java.nio.file.Files
  val jsFileNames = List("client-jsdeps.min.js", "client-opt.js", "client-opt.js.map")
  val dir = baseDirectory.value
  jsFileNames.foreach(name =>
      Files.copy(
        new File(dir, s"client/target/scala-2.12/$name").toPath,
        new File(dir, s"server/src/main/resources/js/$name").toPath,
        java.nio.file.StandardCopyOption.REPLACE_EXISTING
      )
  )
}

commands += Command.command("checkCoverage") { state =>
  "coverage" :: "sharedJVM/clean" :: "sharedJVM/test" :: "coverageReport" :: state
}

commands += Command.command("build") { state =>
  "client/fullOptJS" :: "copyJs" :: "server/assembly" :: state
}
