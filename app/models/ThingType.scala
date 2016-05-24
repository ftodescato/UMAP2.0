package models

import java.util.UUID
import play.api.libs.json.Json

/**
 * The thingType object.
 *
 * @param thingTypeID The unique ID of the thingType.
 * @param thingTypeName The last name of the thingType.
 * @param companyID The list of company ID of thingType.
 */

case class ThingType(
  thingTypeID: UUID,
  thingTypeName: String,
  companyID: List[UUID]
   )


/**
 * The companion object.
 */
object ThingType {

  /**
   * Converts the [ThingType] object to Json and vice versa.
   */
  implicit val jsonFormatThingType = Json.format[ThingType]

}
