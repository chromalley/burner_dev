scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
   "io.spray" % "spray-routing" % "1.3.1",
   "io.spray" % "spray-json_2.11" % "1.3.2",
   "com.typesafe.akka" % "akka-actor_2.11" % "2.4.2"
)

// credit: https://tpolecat.github.io/2014/04/11/scalac-flags.html
scalacOptions ++= Seq(
  "-deprecation",           
  "-encoding", "UTF-8",       // yes, this is 2 args
  "-feature",                
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xfatal-warnings",       
  "-Xlint",
  "-Yno-adapted-args",       
  "-Ywarn-dead-code",        // N.B. doesn't work well with the ??? hole
  "-Ywarn-numeric-widen",   
  "-Ywarn-value-discard",
  "-Xfuture",
  "-Ywarn-unused-import"     // 2.11 only
)