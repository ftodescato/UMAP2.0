package models

import java.util.UUID
import play.api.libs.json.Json
import play.api.libs.json._


 case class DetectionDouble(
  sensor: String,
  value: Double
)

object DetectionDouble {

   /**
    * Converts the [Detection] object to Json and vice versa.
    */
   implicit val jsonFormatDetection = Json.format[DetectionDouble]
}
