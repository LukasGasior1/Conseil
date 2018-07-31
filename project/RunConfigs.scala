import sbt._
import sbt.Keys._

object Runners {
  def runIn(conf: ConfigKey) =
    Defaults.runTask(
      fullClasspath in Runtime,
      mainClass in conf,
      runner in conf
    )

  lazy val devConfigOption = "-Dconfig.resource=ivano-dev.conf"

  lazy val Lorre = config("lorre") extend(Compile) describedAs("lorre-specific settings")
  lazy val Conseil = config("conseil") extend(Compile) describedAs("conseil-specific settings")

  lazy val develOpts = Seq(
    devConfigOption,
    "-XX:+CMSClassUnloadingEnabled",
    "-Xss1M"
  )

  lazy val lorreOpts =
    develOpts ++
    Seq(
      "-Xms512M",
      "-Xmx4096M"
    )

  lazy val conseilOpts =
    develOpts ++
    Seq(
      "-Xmx1024M"
    )

  lazy val testCoverageOpts = Seq(
    devConfigOption,
    "-Xms512M",
    "-Xmx1024M"
  )

}