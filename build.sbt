import com.typesafe.tools.mima.plugin.MimaKeys
import sbt.impl.GroupArtifactID

crossScalaVersions in Global := Seq("2.10.6", "2.11.8")

scalaVersion in Global := crossScalaVersions.value.head

val previousVersion = settingKey[Option[String]]("The artifact version for MiMa to compare against when checking for binary incompatibilities prior to release.") in GlobalScope

previousVersion := None

val failOnBinaryIncompatibility = settingKey[Boolean]("Whether the release should fail when a binary incompatibility is detected") in GlobalScope

failOnBinaryIncompatibility := true

publishMavenStyle := true

publishArtifact := false

publishTo in Global := Some {
  if (isSnapshot.value) Opts.resolver.sonatypeSnapshots
  else                  Opts.resolver.sonatypeStaging
}


val sharedSettings = mimaDefaultSettings ++ Seq[Def.Setting[_]](
  name := "scala-commons-" + name.value,
  homepage := Some(url("https://github.com/programmiersportgruppe/scala-commons")),
  scmInfo := Some(ScmInfo(
    browseUrl   = url("https://github.com/programmiersportgruppe/scala-commons"),
    connection  = "scm:git:git@github.com:programmiersportgruppe/scala-commons.git"
  )),
  licenses := Seq("MIT Licence" -> url("http://opensource.org/licenses/MIT")),
  organization := "org.programmiersportgruppe.scala-commons",
  conflictManager := ConflictManager.strict,
  dependencyOverrides += "org.scala-lang" % "scala-library" % scalaVersion.value,
  dependencyOverrides += "org.scala-lang.modules" %% "scala-xml" % "1.0.5",
  scalacOptions := Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
    "-Xlint",
    "-Xfatal-warnings",
    "-Yclosure-elim",
    "-Ydead-code",
    "-Yno-adapted-args",
    "-Ywarn-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-inaccessible",
    "-Ywarn-nullary-override",
    "-Ywarn-nullary-unit",
    "-Ywarn-numeric-widen",
//    "-Ywarn-value-discard", // possibly more trouble than it's worth
    "-Xfuture") ++ (
    if (scalaBinaryVersion.value == "2.10") Nil
    else Seq(
      "-explaintypes",
      "-Yconst-opt",
      "-Ywarn-infer-any",
      "-Ywarn-unused",
      "-Ywarn-unused-import")
    ),
  pomExtra := {
    <developers>
      <developer>
        <id>fleipold</id>
        <name>Felix Leipold</name>
        <url>https://github.com/fleipold</url>
      </developer>
    </developers>
  },
  testOptions in Test += Tests.Argument("-oF"),
  autoAPIMappings := true,
  apiMappings ++= {
    def jar(artifact: GroupArtifactID): Option[File] = {
      val reference = CrossVersion(scalaVersion.value, scalaBinaryVersion.value)(artifact % "_")
      (for {
        entry <- (fullClasspath in Runtime).value ++ (fullClasspath in Test).value
        module <- entry.get(moduleID.key)
        if module.organization == reference.organization && module.name == reference.name
      } yield entry.data).headOption
    }
    Seq[(GroupArtifactID, sbt.URL)](
      "com.typesafe" %% "config" -> url("http://typesafehub.github.io/config/latest/api/")
    ).flatMap { case (lib, url) => jar(lib).map(_ -> url) }.toMap
  },
  MimaKeys.previousArtifacts := previousVersion.value.map(v => projectID.value.copy(revision = v, explicitArtifacts = Nil)).toSet,
  MimaKeys.failOnProblem := failOnBinaryIncompatibility.value
)

lazy val basics = project
  .settings(sharedSettings: _*)
  .settings(
    description := "Makes working with the scala standard library more fun.",
    libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"
  )

def basicsDependency = basics % "compile->compile;test->test"

lazy val json = project
  .dependsOn(basicsDependency)
  .settings(sharedSettings: _*)
  .settings(
    description := "What json4s isn't bringing to the table.",
    libraryDependencies += "org.json4s" %% "json4s-native" % "3.3.0"
  )
