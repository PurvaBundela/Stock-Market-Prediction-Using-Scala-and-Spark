import sbt.{ExclusionRule, Resolver}

name := """play-scala-starter-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += Resolver.sonatypeRepo("snapshots")

scalaVersion := "2.11.8"

//crossScalaVersions := Seq("2.11.12", "2.12.4")

//libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "com.h2database" % "h2" % "1.4.196"
////dependency override for jackson
//dependencyOverrides ++= Set("com.fasterxml.jackson.core" % "jackson-databind" % "2.6.1")

//fixing java.lang.ClassNotFoundException: de.unkrig.jdisasm.Disassembler
libraryDependencies += "org.codehaus.janino" % "janino" % "3.0.7"

// https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.0"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.11"

//libraryDependencies += "org.apache.kafka" % "kafka-clients" % "0.10.0.0"

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.34"
libraryDependencies += "com.typesafe.play" %% "play-slick" % "3.0.3"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "3.0.3"

//spark
libraryDependencies += "org.apache.spark" % "spark-streaming_2.11" % "2.3.0"
//libraryDependencies += "org.apache.spark" % "spark-streaming-kafka-0-10_2.11" % "2.1.0"
libraryDependencies ++= Seq("org.apache.spark" %% "spark-mllib" % "2.3.0")

// https://mvnrepository.com/artifact/org.apache.spark/spark-core_2.11
libraryDependencies += "org.apache.spark" % "spark-core_2.11" % "2.3.0"

// https://mvnrepository.com/artifact/org.apache.spark/spark-sql_2.11
libraryDependencies += "org.apache.spark" % "spark-sql_2.11" % "2.3.0"

//dependency override for jackson
//dependencyOverrides ++= Set("com.fasterxml.jackson.core" % "jackson-databind" % "2.6.1")

// https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.1"

// https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-annotations" % "2.6.1"


//fixing java.lang.ClassNotFoundException: de.unkrig.jdisasm.Disassembler
libraryDependencies += "org.codehaus.janino" % "janino" % "3.0.7"

// Resolver is needed only for SNAPSHOT versions
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
    "com.adrianhurt" %% "play-bootstrap" % "1.4-P26-B4-SNAPSHOT"
)

libraryDependencies += guice

// https://mvnrepository.com/artifact/com.typesafe.play/play-logback
libraryDependencies += "com.typesafe.play" %% "play-logback" % "2.6.13"

libraryDependencies += "com.github.fommil.netlib" % "all" % "1.1.2"

// https://mvnrepository.com/artifact/com.cloudera.sparkts/sparkts
libraryDependencies += "com.cloudera.sparkts" % "sparkts" % "0.4.0"

// https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients
//libraryDependencies += "org.apache.kafka" % "kafka-clients" % "0.10.0.0"


// https://mvnrepository.com/artifact/org.apache.hbase/hbase
libraryDependencies += "org.apache.hbase" % "hbase" % "1.2.4"

// https://mvnrepository.com/artifact/org.apache.hbase/hbase-shaded-client
libraryDependencies += "org.apache.hbase" % "hbase-shaded-client" % "1.2.4"

// https://mvnrepository.com/artifact/org.apache.hbase/hbase-client
libraryDependencies += "org.apache.hbase" % "hbase-client" % "1.2.4"

// https://mvnrepository.com/artifact/org.apache.hbase/hbase-server
libraryDependencies += "org.apache.hbase" % "hbase-server" % "1.2.4" excludeAll  ExclusionRule(organization = "org.mortbay.jetty")

// https://mvnrepository.com/artifact/org.apache.hbase/hbase-common
libraryDependencies += "org.apache.hbase" % "hbase-common" % "1.2.4"


libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "2.6.0" excludeAll (ExclusionRule(organization = "org.eclipse.jetty"))

libraryDependencies += "org.apache.hadoop" % "hadoop-mapreduce-client-core" % "2.6.0"

// https://mvnrepository.com/artifact/net.jpountz.lz4/lz4
//libraryDependencies += "net.jpountz.lz4" % "lz4" % "1.2.0"







