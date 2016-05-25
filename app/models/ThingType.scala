package models

import java.util.UUID
import play.api.libs.json.Json



case class ThingType(
  thingTypeID: UUID,
  thingTypeName: String,
  companyID: List[UUID],
  valuesInt: Option[List[Map[String, Int]]],
  valuesString: Option[List[Map[String, String]]],
  valuesFloat: Option[List[Map[String, Float]]],
  valuesDouble: Option[List[Map[String, Double]]]
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
