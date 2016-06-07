package forms.thing

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import java.util.{Date, Locale}
import java.text.DateFormat
import java.text.DateFormat._
import models.DetectionDouble
import java.util.UUID



object AddDetectionDouble {

  /**
   * A play framework form.
   */
  val form = Form(
    mapping(
      "measurementsID" -> uuid,
      "sensor" -> nonEmptyText,
      "value" -> longNumber
        )(Data.apply)(Data.unapply)
  )


  case class Data(
    measurementsID: UUID,
    sensor: String,
    value: Long
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
