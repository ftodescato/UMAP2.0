package models

import java.util.UUID
import play.api.libs.json.Json
import play.api.libs.json._
import java.util.{Date, Locale}
import java.text.DateFormat
import java.text.DateFormat._
import java.util.Date


 case class Graphic(
  futureV: Boolean,
  valuesY: Array[Double],
  valuesX: Array[Date],
  resultFunction: Double
)

object Graphic {

   /**
    * Converts the [Graphic] object to Json and vice versa.
    */
   implicit val jsonFormatDetection = Json.format[Graphic]
}
