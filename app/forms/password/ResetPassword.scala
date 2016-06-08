package forms.password

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json


object ResetPassword {

  /**
   * A play framework form.
   */
  val form = Form(
    mapping(
      "email" -> email,
      "secretString" -> nonEmptyText,
      "newPassword" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  /**
   * The form data.

   */
  case class Data(
    email: String,
    secretString: String,
    newPassword: String
  )

  /**
  * The companion object.
  */
  object Data {

  implicit val jsonFormat = Json.format[Data]
  }
}
