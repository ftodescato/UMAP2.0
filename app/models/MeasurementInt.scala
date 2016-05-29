package models

import java.util.UUID
import play.api.libs.json.Json
import java.util.{Date, Locale}
import java.text.DateFormat
import java.text.DateFormat._
import play.api.libs.json._
import play.api.libs.json.OWrites
import play.api.libs.functional.syntax._



 case class MeasurementsInt(
  name: String,
  time: Date,
  value: Int
) extends Measurements

object MeasurementsInt {

  /**
   * Converts the [MeasurementsInt] object to Json and vice versa.
   */
   val writes: Writes[MeasurementsInt] = (
     (__ \ 'name).write[String] and
     (__ \ 'time).write[Date] and
    (__ \ 'value).write[Int]
   )(unlift(MeasurementsInt.unapply))

}
