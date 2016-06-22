package forms.user

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import java.util.UUID

/**
 * The form which handles the sign up process.
 */
object SignUpAdmin {

  /**
   * A play framework form.
   */
  val form = Form(
    mapping(
      "name" -> nonEmptyText,
      "surname" -> nonEmptyText,
      "email" -> email,
      "password" -> nonEmptyText,
      "role" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )


  case class Data(
    name: String,
    surname: String,
    email: String,
    password: String,
    role: String
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
