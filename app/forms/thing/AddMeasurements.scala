package forms.thing

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import java.util.{Date, Locale}
import java.text.DateFormat
import java.text.DateFormat._
import models._
import java.util.UUID
import java.util.Date
import scala.collection.mutable.ListBuffer
import play.api.data.format.Formats._



object AddMeasurement {

  /**
   * A play framework form.
   */
  val form = Form(
    mapping(
      "thingID" -> uuid,
      "dataTime" -> date,
      "sensor" -> list(text),
      "value" -> list(of(doubleFormat)),
      "label" -> of(doubleFormat)
        )(Data.apply)(Data.unapply)
  )


  case class Data(

    thingID: UUID,
    dataTime: Date,
    sensor: List[String],
    value: List[Double],
    label: Double
  )


  /**
   * The companion object.
   */
  object Data {

    /**
     * Converts the [Date] object to Json and vice versa.
     */
    implicit val jsonFormat = Json.format[Data]
  }
}
