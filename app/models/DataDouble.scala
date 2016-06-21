package models

import java.util.UUID
import play.api.libs.json._
import scala.collection.mutable.ListBuffer



case class DataDouble(
  inUse: Boolean,
  infos: ListBuffer[Info]
)


object DataDouble {

   /**
   * Converts the [Data] object to Json and vice versa.
   */
   implicit val jsonFormatData = Json.format[DataDouble]

 }
