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
  functionName: String,
  thingID: Option[UUID],
  thingTypeID: Option[UUID],
  infoDataName: ListBuffer[String]
  var risultati =Array.empty[Double]
  risultati:+"funzione dell'engine"
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
