
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.4.1"

lazy val root = (project in file("."))
  .settings(
    name := "test",
    // Add JavaFX dependencies for your OS: Windows ('win'), Linux ('linux'), or macOS ('mac')
    libraryDependencies ++= Seq(
      "org.openjfx" % "javafx-base" % "17" classifier "win",
      "org.openjfx" % "javafx-controls" % "17" classifier "win",
      "org.openjfx" % "javafx-graphics" % "17" classifier "win",
      "org.openjfx" % "javafx-fxml" % "17" classifier "win",
      "org.openjfx" % "javafx-web" % "17" classifier "win",   // If using HTMLEditor
      "org.openjfx" % "javafx-swing" % "17" classifier "win", // Add this line for JFXPanel
      "org.scalafx" %% "scalafx" % "16.0.0-R25",
      "com.typesafe.slick" %% "slick" % "3.5.1",
      "com.typesafe.slick" %% "slick-codegen" % "3.5.1",
      "org.slf4j" % "slf4j-nop" % "2.0.13",
      "org.postgresql" % "postgresql" % "42.7.3",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.5.1",
      "com.github.tminglei" %% "slick-pg" % "0.22.0",
      "com.github.tminglei" %% "slick-pg_play-json" % "0.22.0",
      "com.github.tototoshi" %% "scala-csv" % "1.3.10",
      ("org.scalaj" %% "scalaj-http" % "2.4.2").cross(CrossVersion.for3Use2_13),
      "org.json4s" %% "json4s-native" % "4.1.0-M6",
      "com.lihaoyi" %% "requests" % "0.8.3",
      "com.lihaoyi" %% "ujson" % "3.3.1",
      "com.softwaremill.sttp.client4" %% "core" % "4.0.0-M16"
    )
  )
