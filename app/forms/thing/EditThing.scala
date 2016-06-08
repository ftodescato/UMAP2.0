package forms.thing

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import java.util.UUID



object EditThing {

  /**
   * A play framework form.
   */
  val form = Form(
    mapping(
      "name" -> nonEmptyText,
      "serialNumber" -> nonEmptyText,
      "description" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )


  case class Data(
    name: String,
    serialNumber: String,
    description: String
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
