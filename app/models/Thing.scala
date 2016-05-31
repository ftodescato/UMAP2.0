package models

import java.util.UUID
import play.api.libs.json.Json
import play.api.libs.json._
import play.api.libs.json.OWrites
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


/**
 * The companion object.
 */
 // object Thing {
 //
 //   /**
 //    * Converts the [Data] object to Json and vice versa.
 //    */
 //    implicit def thingWrites: Writes[Thing] = (
 //      (__ \ 'thingID).write[UUID] and
 //      (__ \ 'name).write[String] and
 //      (__ \ 'serialNumber).write[String] and
 //      (__ \ 'description).write[String] and
 //      (__ \ 'thingTypeID).write[UUID] and
 //      (__ \ 'company).write[UUID]
 //    //  (__ \ 'datas).write[List[UUID]]
 //    )(unlift(Thing.unapply))
 //
 //    implicit def thingReads: Reads[Thing] = (
 //      (__ \ 'thingID).read[UUID] and
 //      (__ \ 'name).read[String] and
 //      (__ \ 'serialNumber).read[String] and
 //      (__ \ 'description).read[String] and
 //      (__ \ 'thingTypeID).read[UUID] and
 //      (__ \ 'company).read[UUID]
 //  //    (__ \ 'datas).read[List[UUID]]
 //    )(Thing.apply _)
 //
 // }
