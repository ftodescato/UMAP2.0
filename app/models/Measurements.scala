package models

import java.util.UUID
import play.api.libs.json._
import java.util.{Date, Locale}
import java.text.DateFormat
import java.text.DateFormat._


case class Measurements(
  dataTime: Date,
  sensors: List[Detection],
  healty: Boolean
)

object Measurements {

  /**
   * Converts the [Measurements] object to Json and vice versa.
   */
  implicit val jsonFormatMeasurement = Json.format[Measurements]

}
