import _root_.sbtassembly.AssemblyPlugin.autoImport._
import _root_.sbtassembly.PathList

name := "Bk_Scala"

version := "1.0"

scalaVersion := "2.11.5"

libraryDependencies ++= {
  val AkkaVersion       = "2.4.2"
  Seq(
    "com.typesafe.akka" %% "akka-http-experimental" % AkkaVersion,
    "com.typesafe.akka" %% "akka-cluster" % AkkaVersion,
    "com.typesafe.akka" %% "akka-cluster-tools" % AkkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % "test",
    "net.liftweb" % "lift-json_2.11" % "2.6.3",
    "org.reactivemongo" %% "reactivemongo" % "0.11.10",
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "org.scalatest"        %%    "scalatest"    	      %      "2.2.5"     %    "test",
    "com.typesafe.akka" %% "akka-http-testkit-experimental" % "2.0.3" %    "test",
    "org.scalacheck" %% "scalacheck" % "1.12.5" % "test"
  )
}

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "Typesafe" at "https://repo.typesafe.com/typesafe/releases/"

//mainClass in assembly := some("com.service.HttpService")
//assemblyJarName := "runnableNode.jar"

mainClass in assembly := some("WorkerNode.startManager")
assemblyJarName := "workerNode.jar"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case "application.conf"            => MergeStrategy.concat
  case "reference.conf"              => MergeStrategy.concat
  case PathList("reference.conf") => MergeStrategy.concat
  case x =>
    val baseStrategy = (assemblyMergeStrategy in assembly).value
    baseStrategy(x)
}

fork in run := true