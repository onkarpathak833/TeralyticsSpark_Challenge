package com.example.project.domain

import org.apache.spark.sql.execution.streaming.FileStreamSource.Timestamp

case class PersonMovement(timestamp: Timestamp, x: Double, y: Double, floor: Int, uid: String)
