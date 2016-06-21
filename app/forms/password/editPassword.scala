package forms.password

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json


object EditPassword {

  /**
   * A play framework form.
   */
  val form = Form(
    mapping(
      "newPassword" -> nonEmptyText,
      "newsecretString" ->nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  /**
   * The form data.

   */
  case class Data(
    newPassword: String,
    newsecretString: String
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
