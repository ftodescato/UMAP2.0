package models

import java.util.UUID
import play.api.libs.json.Json



case class Thing(
  thingID: UUID,
  serialNumber: String,
  thingPassword: String,
  thingName: String,
  companyID: UUID,
  thingTypeID: UUID,

   )


/**
 * The companion object.
 */
object Thing {

  /**
   * Converts the [Thing] object to Json and vice versa.
   */
  implicit val jsonFormatThing = Json.format[Thing]

}
