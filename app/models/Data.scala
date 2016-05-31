package models

import java.util.UUID
import play.api.libs.json._


case class Data(
  inUse: Boolean,
  valuee: List[String]
)


object Data {

   /**
   * Converts the [Data] object to Json and vice versa.
   */
   implicit val jsonFormatData = Json.format[Data]

 }
