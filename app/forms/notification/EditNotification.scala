package forms.notification

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.data.format.Formats._


object EditNotification {

  /**
   * A play framework form.
   */
  val form = Form(
    mapping(
      "description" -> nonEmptyText,
      "parameter" -> nonEmptyText,
      "minValue" -> of(doubleFormat),
      "maxValue" -> of(doubleFormat)
    )(Data.apply)(Data.unapply)
  )


  case class Data(
    description: String,
    parameter: String,
    minValue: Double,
    maxValue: Double
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
