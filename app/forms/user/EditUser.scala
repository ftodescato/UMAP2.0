package forms.user

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import java.util.UUID

/**
 * The form which handles the sign up process.
 */
object EditUser {

  /**
   * A play framework form.
   */
  val form = Form(
    mapping(
      "name" -> nonEmptyText,
      "surname" -> nonEmptyText,
      "olEmail" -> email,
      "email" -> email,
      "password" -> nonEmptyText,
      "company" -> uuid,
      "role"  -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  /**
   * The form data.

   */
  case class Data(
    name: String,
    surname: String,
    oldEmail: String,
    email: String,
    password: String,
    company: UUID,
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
