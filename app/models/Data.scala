package models

import java.util.UUID
import play.api.libs.json._
import scala.collection.mutable.ListBuffer



case class Data(
  inUse: Boolean,
  valuee: ListBuffer[Info]
)


object Data {

   /**
   * Converts the [Data] object to Json and vice versa.
   */
   implicit val jsonFormatData = Json.format[Data]

 }
