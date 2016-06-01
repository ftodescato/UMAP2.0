package models

import java.util.UUID
import play.api.libs.json.Json

/**
 * The notification object.
 *
 * @param notificationID The unique ID of the notification.
 * @param notificationDescription Maybe the description of the notification.
 * @param emailUser The email of user where arrive the notification.
 * @param inputType The input type of object.
 * @param thingTypeID Maybe the object's model bound to notification.
 * @param thingID Maybe the object bound to notification.
 * @param valMin Maybe the minimum value of input type.
 * @param valMax Maybe the maximum value of input type.
 *
 */
case class Notification(
  notificationID: UUID,
  notificationDescription: Option[String],
  emailUser: String,
  inputType: String,
  thingTypeID: Option[UUID],
  thingID: Option[UUID],
  valMin: Option[Double],
  valMax: Option[Double]
   )


/**
 * The companion object.
 */
object Notification {

  /**
   * Converts the [Notification] object to Json and vice versa.
   */
  implicit val jsonFormatNotification = Json.format[Notification]

}
