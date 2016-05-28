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
      "thingName" -> nonEmptyText,
      "serialNumber" -> nonEmptyText,
      "description" -> nonEmptyText,
      "thingTypeID" -> uuid,
      "password" -> nonEmptyText,
      "company" -> uuid
    )(Data.apply)(Data.unapply)
  )


  case class Data(
    thingName: String,
    serialNumber: String,
    description: String,
    thingTypeID: UUID,
    password: String,
    company: UUID)


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
