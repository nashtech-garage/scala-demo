import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd}
import sbt.Keys._

ThisBuild / version := "1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.10"
ThisBuild / javacOptions ++= Seq("-source", "1.8", "-target", "1.8")
ThisBuild / scalacOptions ++= Seq(
  "-encoding", "UTF-8", // yes, this is 2 args
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  "-Ywarn-dead-code", // Warn when dead code is identified.
//  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xlint:-unused,_",
  "-Xsource:2.13.10"
)
ThisBuild / Test / scalacOptions ++= Seq("-Yrangepos")
ThisBuild / Compile / doc / scalacOptions ++= Seq("-no-link-warnings")

// Avoid some of the constant SBT "Updating"
updateOptions := updateOptions.value.withCachedResolution(true)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "scala-demo",
    libraryDependencies ++= (appDependencies ++ testDependencies).map(excludeBadTransitiveDependencies),
    PlayKeys.devSettings := Seq("play.server.http.port" -> "8080")
  )

// Separated project for running integration test
lazy val integration = (project in file("it"))
  .dependsOn(root, root % "test->test") // get access to the common test classes
  .settings(
    name := "integration-test",
    libraryDependencies ++= testDependencies.map(excludeBadTransitiveDependencies),
    sourceDirectory := baseDirectory.value,
    Test / testOptions ++= Seq(
      Tests.Argument(TestFrameworks.ScalaTest, "-o"), // console out
      Tests.Argument(TestFrameworks.ScalaTest, "-h", s"${target.value}/test-html")
    ),
    Test / test := (Test / test).dependsOn(root / Assets / packageBin).value,
    // Forking changes the working dir which breaks where we look for things, so don't fork for now.
    // May be able to fix some other way by updating ForkOptions.
    Test / fork := false,
    Test / parallelExecution := false,
    // Make assets available so they have styles and scripts
    Test / managedClasspath += (root / Assets / packageBin).value,
  )

// Dependencies
val playSilhouetteVersion = "6.1.1"
val slickVersion = "3.3.3"
val playSlickVersion = "5.0.0"
val akkaVersion = "2.6.20"
val enumeratumVersion = "1.7.0"
val enumeratumSlickVersion = "1.5.16"

val appDependencies = Seq(
  guice, // DI library
  ws,
  filters,

  // DB Access Library
  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.typesafe.play" %% "play-slick" % playSlickVersion,
  "com.typesafe.play" %% "play-slick-evolutions" % playSlickVersion,
  "org.postgresql" % "postgresql" % "42.2.10", // PostgreSQL JDBC Driver

  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % "10.1.15",

  "org.joda" % "joda-convert" % "2.2.3",

  "org.scala-lang.modules" %% "scala-java8-compat" % "1.0.2",

  "net.logstash.logback" % "logstash-logback-encoder" % "6.3",
  "net.codingwell" %% "scala-guice" % "4.2.11", // Scala extensions for Google Guice, Allows binding via type parameters
  "io.lemonlabs" %% "scala-uri" % "4.0.3",

  // For JWT Authentication
  "com.mohiva" %% "play-silhouette" % playSilhouetteVersion,
  "com.mohiva" %% "play-silhouette-password-bcrypt" % playSilhouetteVersion,
  "com.mohiva" %% "play-silhouette-persistence" % playSilhouetteVersion,
  "com.mohiva" %% "play-silhouette-crypto-jca" % playSilhouetteVersion,

  "com.beachape" %% "enumeratum" % enumeratumVersion,
  "com.beachape" %% "enumeratum-play" % enumeratumVersion,
  "com.beachape" %% "enumeratum-play-json" % enumeratumVersion,
  "com.beachape" %% "enumeratum-slick" % enumeratumSlickVersion,

  // Slick extensions for PostgreSQL, to support a series of pg data types and related operators/functions.
  // https://github.com/tminglei/slick-pg
  //  "com.github.tminglei" %% "slick-pg" % "0.18.1",
  //  "com.github.tminglei" %% "slick-pg_play-json" % "0.18.1",
)

val testDependencies = Seq(
  specs2,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.opentable.components" % "otj-pg-embedded" % "0.13.4", // https://github.com/opentable/otj-pg-embedded
  "org.mockito" % "mockito-core" % "4.0.0",
  "org.mockito" % "mockito-scala_2.13" % "1.17.14",
  "com.mohiva" %% "play-silhouette-testkit" % playSilhouetteVersion,
  "org.pegdown" % "pegdown" % "1.5.0"
).map(_ % Test)

def excludeBadTransitiveDependencies(mod: ModuleID): ModuleID = mod.excludeAll(
  ExclusionRule(organization = "commons-logging"),
  ExclusionRule(organization = "org.slf4j", name = "slf4j-log4j12")
)

resolvers += Resolver.jcenterRepo
resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

//resolvers += ("Local Maven Repository" at "file:///" + Path.userHome.absolutePath + "/.m2/repository")
//resolvers += Resolver.bintrayRepo("scalaz", "releases")
//resolvers ++= Resolver.sonatypeOssRepos("releases")
//resolvers += "Atlassian's Maven Public Repository" at "https://packages.atlassian.com/maven-public/"
//resolvers += "JCenter repo" at "https://packages.atlassian.com/maven-public/"
//resolvers += ("Local Maven Repository" at "file:///" + Path.userHome.absolutePath + "/.m2/repository")
//resolvers += "SBT plugins" at "https://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"

Universal / javaOptions ++= Seq(
  // JVM memory tuning
  "-J-Xmx1024m",
  "-J-Xms128m",
  // Since play uses separate pidfile we have to provide it with a proper path
  // name of the pid file must be play.pid
  s"-Dpidfile.path=/opt/docker/${packageName.value}/run/play.pid"
)

// use ++= to merge a sequence with an existing sequence
dockerCommands ++= Seq(
  Cmd("USER", "root"),
  ExecCmd("RUN", "mkdir", "-p", s"/opt/docker/${packageName.value}"),
  ExecCmd("RUN", "mkdir", "-p", s"/opt/docker/${packageName.value}/run"),
  ExecCmd("RUN", "chown", "-R", "daemon:daemon", s"/opt/docker/${packageName.value}/"),
  ExecCmd("RUN", "mkdir", "-p", "/opt/docker/logs/"),
  ExecCmd("RUN", "chmod", "+w", "-R", "/opt/docker/logs/")
)

// exposing the play ports
Docker / dockerExposedPorts := Seq(8080, 8443)
Docker / dockerExposedVolumes := Seq("/opt/docker/logs")
