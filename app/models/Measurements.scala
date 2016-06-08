package models

import play.api.libs.json._
import java.util.{Date, Locale}
import java.text.DateFormat
import java.text.DateFormat._
import scala.collection.mutable.ListBuffer
import java.util.Date
import java.util.UUID


case class Measurements(
  measurementsID: UUID,
  thingID: UUID,
  dataTime: Date,
  sensors: List[DetectionDouble],
  label: Double
)

object Measurements {

  /**
   * Converts the [Measurements] object to Json and vice versa.
   */
  implicit val jsonFormatMeasurement = Json.format[Measurements]

}
