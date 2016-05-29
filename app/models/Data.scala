package models

import java.util.UUID
import play.api.libs.json._
import play.api.libs.json.OWrites
import play.api.libs.functional.syntax._

case class Data(
  dataID: UUID,
  thingID: UUID,
  measurements: List[Measurements]
)


// object Data {
//
//   /**
//    * Converts the [Data] object to Json and vice versa.
//    */
//   implicit val jsonFormatData = Json.format[Data]
//
// }


/**
 * The companion object.
 */
object Data {

  /**
   * Converts the [Data] object to Json and vice versa.
   */
   implicit def dataWrites: Writes[Data] = (
     (__ \ 'dataID).write[UUID] and
     (__ \ 'thingID).write[UUID] and
     (__ \ 'measurements).write[List[Measurements]]
   )(unlift(Data.unapply))

}
