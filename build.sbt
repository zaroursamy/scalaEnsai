name := "scalaEnsai"

version := "1.0"

scalaVersion := "2.11.8"

val sparkV = "2.2.0"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkV,
  "org.apache.spark" %% "spark-sql" % sparkV,
  "org.apache.spark" %% "spark-mllib" % sparkV
)