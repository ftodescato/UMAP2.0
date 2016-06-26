package models

import java.util.UUID
import play.api.libs.json.Json
import play.api.libs.json._


 case class Info(
  name: String,
  visible: Boolean
)

object Info {

   /**
    * Converts the [Info] object to Json and vice versa.
    */
   implicit val jsonFormatDetection = Json.format[Info]
}
