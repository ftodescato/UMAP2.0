package models

import java.util.UUID
import play.api.libs.json.Json
import scala.collection.mutable.ListBuffer

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
  thingID: UUID,
  thingTypeID: UUID,
  infoDataName: ListBuffer[String]
  //risultati: Array[Double]

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
