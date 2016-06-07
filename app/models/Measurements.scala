package models

import play.api.libs.json._
import java.util.{Date, Locale}
import java.text.DateFormat
import java.text.DateFormat._
import scala.collection.mutable.ListBuffer

import java.util.UUID


case class Measurements(
  measurementsID: UUID,
  thingID: UUID,
  dataTime: String,
  sensors: ListBuffer[DetectionDouble],
  healty: Boolean
)

object Measurements {

  /**
   * Converts the [Measurements] object to Json and vice versa.
   */
  implicit val jsonFormatMeasurement = Json.format[Measurements]

}
