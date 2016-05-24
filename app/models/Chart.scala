package models

import java.util.UUID
import play.api.libs.json.Json

/**
 * The chart object.
 *
 * @param chartID The unique ID of the chart.
 * @param chartName Maybe the chart's name.
 * @param thingID Maybe the thingID.
 * @param thingTypeID Maybe the thingTypeID.
 *
 */
case class Chart(
  chartID: UUID,
  chartName: Option[String],
  thingID: Option[UUID],
  thingTypeID: Option[UUID]
   )


/**
 * The companion object.
 */
object Chart {

  /**
   * Converts the [Chart] object to Json and vice versa.
   */
  implicit val jsonFormatChart = Json.format[Chart]

}
