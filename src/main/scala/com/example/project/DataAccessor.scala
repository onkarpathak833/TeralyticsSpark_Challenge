package com.example.project

import java.sql.Date
import java.text.SimpleDateFormat
import java.time.{Instant, LocalDateTime, ZoneId}
import java.util.TimeZone

import com.example.project.DataAccessor.{buildMeetingDetailObject, convertDateStringToEpoch, convertTimestampToString}
import com.example.project.constants.AppContants
import com.example.project.constants.AppContants._
import com.example.project.domain.{MeetingDetail, PersonMovement}
import com.typesafe.config.Config
import org.apache.spark.sql.execution.streaming.FileStreamSource.Timestamp
import org.apache.spark.sql.types.TimestampType
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

class DataAccessor(sparkSession: SparkSession, config: Config) {

  def loadData(): Dataset[PersonMovement] = {
    val dataFilePath = config.getString("data.file_path")
    import sparkSession.implicits._
    val dataSet = sparkSession.read.option("header", "true").option("inferSchema", "true").csv(dataFilePath).as[PersonMovement]
    dataSet
  }

  private def getMovements(uid: String, floorData: Dataset[PersonMovement]) = {
    val uidMovements: Dataset[PersonMovement] = floorData.filter(row => row.uid == uid)
    uidMovements
  }

  def getMeetingDetails(uidOne: String, uidTwo: String, dataset: Dataset[PersonMovement]): Dataset[MeetingDetail] = {

    import org.apache.spark.sql.functions._
    import sparkSession.implicits._

    val uidOneMovements = getMovements(uidOne, dataset)
    val uidOneDf = uidOneMovements.select(from_unixtime(uidOneMovements(TIMESTAMP).cast(LONG), DATE_FORMAT) as DATE_TIME, uidOneMovements(X), uidOneMovements(Y), uidOneMovements(FLOOR), uidOneMovements(UID))


    val uidOneDataset = uidOneDf.map(row => {
      val dateString = row.getAs[String](DATE_TIME)
      val epochSeconds: Timestamp = convertDateStringToEpoch(dateString)
      PersonMovement(epochSeconds, row.getAs[Double](X), row.getAs[Double](Y), row.getAs[Int](FLOOR), row.getAs[String](UID))
    })


    val uidTwoMovements = getMovements(uidTwo, dataset)
    val uidTwoDf = uidTwoMovements.select(from_unixtime(uidTwoMovements(TIMESTAMP).cast(LONG), DATE_FORMAT) as DATE_TIME, uidTwoMovements(X), uidTwoMovements(Y), uidTwoMovements(FLOOR), uidTwoMovements(UID))

    val uidTwoDataset: Dataset[PersonMovement] = uidTwoDf.map(row => {
      val dateString = row.getAs[String](DATE_TIME)
      val epochSeconds: Timestamp = convertDateStringToEpoch(dateString)
      PersonMovement(epochSeconds, row.getAs[Double](X), row.getAs[Double](Y), row.getAs[Int](FLOOR), row.getAs[String](UID))
    })


    val meetingPoints: Dataset[MeetingDetail] = uidOneDataset.join(uidTwoDataset, uidOneDataset(FLOOR) === uidTwoDataset(FLOOR)
      && (abs(uidOneDataset(TIMESTAMP) - uidTwoDataset(TIMESTAMP))) <= 1
      && abs((uidOneDataset(X) - uidTwoDataset(X))) <= 1
      && abs((uidOneDataset(Y) - uidTwoDataset(Y))) <= 1)
      .toDF(UID_1_TIME, UID_1_X, UID_1_Y, MEET_FLOOR, UID_1, UID_2_TIME, UID_2_X, UID_2_Y,FLOOR_2, UID_2).drop(FLOOR_2)
      .map(row => {
        val uidOneTimestamp = row.getAs[Timestamp](UID_1_TIME)
        val uidOneDateTime: String = convertTimestampToString(uidOneTimestamp)

        val uidTwoTimestamp = row.getAs[Timestamp](UID_2_TIME)
        val uidTwoDateTime = convertTimestampToString(uidTwoTimestamp)

        buildMeetingDetailObject(row, uidOneDateTime, uidTwoDateTime)
      })

    meetingPoints

  }


  def ifTwoUidsMeet(uidOne: String, uidTwo: String, meetingDetails : Dataset[MeetingDetail]) : Boolean = {
    val rowCounts = meetingDetails.count()

    rowCounts match {
      case 0 => println("UID1 : "+uidOne+" and UID2 : "+uidTwo+" may not meet each other")
        meetingDetails.show(false)
        false
      case _ => println("UID1 : "+uidOne+" and UID2 :"+uidTwo+" may have met each other at following meeting points "+rowCounts+" times")
        meetingDetails.show(false)
        true
    }
  }


}

object DataAccessor {

  private def convertTimestampToString(dateTimestamp: Timestamp) = {
    val date = new Date(dateTimestamp)
    val dateTimeString = buildSimpleDateFormat.format(date)
    dateTimeString
  }

  private def convertDateStringToEpoch(dateString: String) = {
    val simpleDateFormat: SimpleDateFormat = buildSimpleDateFormat
    val date = simpleDateFormat.parse(dateString)
    val epochSeconds = date.getTime
    epochSeconds
  }

  private def buildSimpleDateFormat: SimpleDateFormat = {
    val simpleDateFormat = new SimpleDateFormat(DATE_FORMAT)
    simpleDateFormat.setTimeZone(TimeZone.getTimeZone(GMT_TIMEZONE))
    simpleDateFormat

  }

  private def buildMeetingDetailObject(row: Row, uidOneDateTime: String, uidTwoDateTime: String) = {

    MeetingDetail(uidOneDateTime,
      row.getAs[Double](UID_1_X),
      row.getAs[Double](UID_1_Y),
      row.getAs[Int](MEET_FLOOR),
      row.getAs[String](UID_1),
      uidTwoDateTime,
      row.getAs[Double](UID_2_X),
      row.getAs[Double](UID_2_Y),
      row.getAs[String](UID_2))
  }


}

