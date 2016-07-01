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
      "notificationDescription" -> nonEmptyText,
      "valMax" -> of(doubleFormat),
      "valMin" -> of(doubleFormat)
    )(Data.apply)(Data.unapply)
  )


  case class Data(
    notificationDescription: String,
    valMax: Double,
    valMin: Double
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
