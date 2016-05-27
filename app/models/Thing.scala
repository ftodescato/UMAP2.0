package models

import java.util.UUID
import play.api.libs.json.Json



case class Thing(
  thingID: UUID,
  name: String,
  serialNumber: String,
  description: String,
  thingTypeID: UUID,
  companyID: UUID
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
