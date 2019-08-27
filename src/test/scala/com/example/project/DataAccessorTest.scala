package com.example.project

import java.io.{File, InputStream}
import java.sql.Timestamp

import com.example.project.domain.PersonMovement
import com.typesafe.config.{Config, ConfigFactory}
import org.mockito.{ArgumentMatchers, Mockito}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpec}

class DataAccessorTest extends WordSpec with Matchers with MockitoSugar with SparkUnitTest {

  val config : Config = mock[Config]
  val testConfigs = ConfigFactory.load("application.conf")
  val testDataPath = testConfigs.getString("data.file_path")

  ".loadData" when {
    "called for given dataset" should {
      "create dataset from given csv data file" in withSparkSession { sparkSession =>

        Mockito.when(config.getString(ArgumentMatchers.anyString())).thenReturn(testDataPath)
        val dataAccessor : DataAccessor = new DataAccessor(sparkSession, config)
        dataAccessor.loadData().collectAsList().size() should be(9)
      }
    }

  }

  ".getMeetingDetails" when {
    "called for a pair of UIDs that meet each other" should {
      "return meeting details" in withSparkSession { sparkSession =>

        import sparkSession.implicits._
        val df = sparkSession.read.option("header","true").option("inferSchema","true").csv(testDataPath).as[PersonMovement]
        val dataAccessor : DataAccessor = new DataAccessor(sparkSession, config)
        val uidOne = "78b85537"
        val uidTwo = "27cb9c6f"
        dataAccessor.getMeetingDetails(uidOne,uidTwo,df).collectAsList().size() should not be(0)
      }
    }

    "called for a pair of UIDs that do not meet each other" should {
      "return empty meeting dataset" in withSparkSession { sparkSession =>


        import sparkSession.implicits._
        val df = sparkSession.read.option("header","true").option("inferSchema","true").csv(testDataPath).as[PersonMovement]
        val dataAccessor : DataAccessor = new DataAccessor(sparkSession, config)
        val uidOne = "78b85537"
        val uidTwo = "285d22e4"
        dataAccessor.getMeetingDetails(uidOne,uidTwo,df).collectAsList().size() should be(0)

      }
    }
  }

}
