package models

import java.util.UUID
import play.api.libs.json.Json

/**
 * The thing object.
 *
 * @param thingID The unique ID of the thing.
 * @param serialNumber The serial number of the thing.
 * @param thingPassword The password of the thing.
 * @param thingName The last name of the thing.
 * @param companyID The unique iD of company bound to the Thing.
 * @param thingTypeID The unique iD of ThingType bound to the Thing.
 * @param valuesInt The Int values of the Thing.
 * @param valuesString The String values of the Thing.
 * @param valuesFloat The Float values of the Thing.
 * @param valuesDouble The Double values of the Thing.
 */

case class Thing(
  thingID: UUID,
  serialNumber: String,
  thingPassword: String,
  thingName: String,
  companyID: UUID,
  thingTypeID: UUID,
  valuesInt: Option[List[Map[String, Int]]],
  valuesString: Option[List[Map[String, String]]],
  valuesFloat: Option[List[Map[String, Float]]],
  valuesDouble: Option[List[Map[String, Double]]]
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
