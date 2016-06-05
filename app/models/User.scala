package models

import java.util.UUID

import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }
import play.api.libs.json.Json


case class User(
  userID: UUID,
  name: String,
  surname: String,
  loginInfo: LoginInfo,
  email: String,
  company: UUID,
  mailConfirmed: Boolean,
  token: String,
  role: String) extends Identity

/**
 * The companion object.
 */
object User {

  /**
   * Converts the [User] object to Json and vice versa.
   */
  implicit val jsonFormat = Json.format[User]
}
