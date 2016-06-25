package models

import java.util.UUID
import play.api.libs.json.Json

import scala.collection.mutable.ListBuffer


case class Chart(
  chartID: UUID,
  functionName: String,
  thingID: UUID,
  infoDataName: String
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
