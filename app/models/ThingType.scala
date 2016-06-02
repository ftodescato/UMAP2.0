package models

import java.util.UUID
import play.api.libs.json.Json
import scala.collection.mutable.ListBuffer



case class ThingType(
  thingTypeID: UUID,
  thingTypeName: String,
  companyID: ListBuffer[UUID],
  doubleValue: Data
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
