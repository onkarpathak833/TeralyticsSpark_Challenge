package com.example.project

import org.apache.spark.sql.{QueryTest, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}

trait SparkUnitTest extends SparkSetup {

  def assertion: QueryTest.type = QueryTest

}


trait SparkSetup {

  def withSparkContext(testMethod: SparkContext => Any) = {

    val sparkContext = buildSparkContext
    try {
      testMethod(sparkContext)
    }
    finally {
      sparkContext.stop()
    }
  }

  def withSparkSession(testMethod: SparkSession => Any) = {

    val context = buildSparkContext
    val sparkSession = SparkSession.builder().config(context.getConf).getOrCreate()
    try {
      testMethod(sparkSession)
    }
    finally {
      sparkSession.stop()
    }
  }

  private def buildSparkContext = {

    val conf = new SparkConf().setMaster("local").setAppName("spark test")
    new SparkContext(conf)
  }

}