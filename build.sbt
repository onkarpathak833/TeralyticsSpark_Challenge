import sbt.Keys.version
import sbt._
import Keys._
import sbt.project

lazy val root = (project in file(".")).
  settings(
    name := "MyProjects",
    version := "0.1",
    scalaVersion := "2.11.8",
    mainClass in Compile := Some("com.example.project.MyApp")
  )

// https://mvnrepository.com/artifact/org.apache.spark/spark-core
libraryDependencies += "org.apache.spark" % "spark-core_2.11" % "2.3.0"

// https://mvnrepository.com/artifact/org.apache.spark/spark-sql
libraryDependencies += "org.apache.spark" % "spark-sql_2.11" % "2.3.0"

libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.0"

// https://mvnrepository.com/artifact/commons-cli/commons-cli
libraryDependencies += "commons-cli" % "commons-cli" % "1.4"

libraryDependencies += "com.typesafe" % "config" % "1.3.2"

//libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"

libraryDependencies += "org.mockito" % "mockito-core" % "2.12.0" % "test"

libraryDependencies += "org.apache.spark" % "spark-core_2.11" % "2.3.0" % "test" classifier "tests"

libraryDependencies += "org.apache.spark" % "spark-sql_2.11" % "2.3.0" % "test" classifier "tests"


resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"
mainClass in assembly := Some("com.example.project.MyApp")

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}



resolvers += Resolver.mavenLocal
