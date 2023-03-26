name := """play-scala-demo"""
organization := "com.example"

version := "1.0-SNAPSHOT"
assemblyJarName in assembly := "play-scala-demo-1.0-SNAPSHOT.jar"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.10"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
// libraryDependencies ++= Seq(
//   "org.scalatest" %% "scalatest" % "3.2.9" % Test
// )
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
