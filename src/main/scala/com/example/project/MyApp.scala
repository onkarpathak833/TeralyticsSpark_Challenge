package com.example.project

import java.io.File

import com.example.project.constants.AppContants.{AppConf, AppName, UID1, UID2}
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.commons.cli.{BasicParser, CommandLine, Options, Option => CliOption}
import org.apache.spark.sql.SparkSession

import scala.util.{Failure, Success, Try}

object MyApp {

  def main(args: Array[String]): Unit = {

    val commandLine = getCommandLineOptions(args)
    val configPath = getConfigFilePath(commandLine)
    val uidOne = getUid1(commandLine)
    val uidTwo = getUid2(commandLine)

    val sparkSession: SparkSession = buildSparkSession(configPath)
    val config : Config = getAppConfigs(configPath)

    val loader = new DataAccessor(sparkSession, config)
    val allMovements = loader.loadData()
    val meetingDetails = loader.getMeetingDetails(uidOne, uidTwo, allMovements)
    loader.ifTwoUidsMeet(uidOne, uidTwo, meetingDetails)

  }

  private def buildSparkSession(configPath: String) = {
    val sparkBuilder = SparkSession.builder().appName(AppName).master("local[*]")
    val config = getAppConfigs(configPath)
    val sparkEnv = config.getString("data.spark_env")

    if (sparkEnv.contains("local")) {
      sparkBuilder.master("local[*]")
    }

    val sparkSession = sparkBuilder.getOrCreate()
    sparkSession
  }

  private def getUid2(commandLine: CommandLine) = {
    commandLine.getOptionValue(UID2)
  }

  private def getUid1(commandLine: CommandLine) = {
    commandLine.getOptionValue(UID1)
  }

  private def getConfigFilePath(commandLine: CommandLine) = {
    commandLine.getOptionValue(AppConf)
  }

  private def getAppConfigs(path: String): Config = {
    val applicationConfigs = ConfigFactory.parseFile(new File(path)).withFallback(ConfigFactory.load("application.conf"))
    applicationConfigs
  }

  private def getCommandLineOptions(args: Array[String]): CommandLine = {

    val options = new Options
    val configOption = new CliOption(AppConf, true, "Application Config File Path")
    configOption.setRequired(true)

    val uidOneOption = new CliOption(UID1, true, "First UID Value")
    uidOneOption.setRequired(true)

    val uidTwoOption = new CliOption(UID2, true, "Second UID Value")
    uidTwoOption.setRequired(true)

    options.addOption(configOption)
    options.addOption(uidOneOption)
    options.addOption(uidTwoOption)

    Try(new BasicParser().parse(options, args)) match {
      case Success(parsedOptions) => parsedOptions
      case Failure(exception) => throw exception
    }

  }

}
