package models

import java.util.UUID
import play.api.libs.json.Json
//import play.api.libs.json._
//import play.api.libs.json.OWrites
import play.api.libs.functional.syntax._
import scala.collection.mutable.ListBuffer


case class Thing(
  thingID: UUID,
  name: String,
  serialNumber: String,
  description: String,
  thingTypeID: UUID,
  companyID: UUID,
  datas: ListBuffer[Measurements]
)
object Thing {

  /**
   * Converts the [Thing] object to Json and vice versa.
   */
  implicit val jsonFormatThing = Json.format[Thing]

}
