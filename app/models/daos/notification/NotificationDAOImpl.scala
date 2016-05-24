package models.daos.notification

import java.util.UUID

import models.Notification

import scala.collection.mutable
import scala.concurrent.Future

import javax.inject.Inject
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.api._

import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection._

/**
 * Give access to the notification object.
 */
class NotificationDAOImpl @Inject() (db : DB) extends NotificationDAO {

  def collection: JSONCollection = db.collection[JSONCollection]("notification")

  /**
   * Finds a notification by model ID.
   *
   * @param thingTypeID The ID of the model to find.
   * @return The found notification or None if no notification for the given modelID could be found.
   */

  def findNotificationOfThingType(thingTypeID: UUID): Future[Option[Notification]] = {
    collection.find(Json.obj("thingTypeID" -> thingTypeID)).one[Notification]
  }

  def findAll(): Future[List[Notification]] = {
    collection.find(Json.obj()).cursor[Notification]().collect[List]()
  }

  /**
   * Finds a notification by its chart ID.
   *
   * @param notificationID The ID of the notification to find.
   * @return The found notification or None if no notification for the given ID could be found.
   */
  def find(notificationID: UUID) : Future[Option[Notification]] = {
    collection.find(Json.obj("notificationID" -> notificationID)).one[Notification]
  }

  /**
   * Saves a notification.
   *
   * @param notification The notification to save.
   * @return The saved notification.
   */
  def save(notification: Notification) = {
    collection.insert(notification)
    Future.successful(notification)
  }

  def update(notificationID: UUID, notification2: Notification) = {
    collection.update(Json.obj("notificationID" -> notificationID), notification2)
  }

  def remove(notificationID: UUID) = {
    collection.remove(Json.obj("notificationID" -> notificationID))
  }
}
