val scala3Version = "3.1.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "Scala Talk",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scodec" %% "scodec-core" % "2.1.0",
    libraryDependencies += "org.scodec" %% "scodec-bits" % "1.1.31",
    libraryDependencies += "dev.optics" %% "monocle-core"  % "3.1.0",
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
  )
