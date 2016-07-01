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

  def findNotificationOfThingType(thingTypeID: UUID): Future[List[Notification]] = {
    collection.find(Json.obj("thingTypeID" -> thingTypeID)).cursor[Notification]().collect[List]()
  }
  def findNotificationOfThing(thingID: UUID): Future[List[Notification]] = {
    collection.find(Json.obj("thingID" -> thingID)).cursor[Notification]().collect[List]()
  }

  def findAll(): Future[List[Notification]] = {
    collection.find(Json.obj()).cursor[Notification]().collect[List]()
  }

  def findByID(notificationID: UUID) : Future[Option[Notification]] = {
    collection.find(Json.obj("notificationID" -> notificationID)).one[Notification]
  }

  def save(notification: Notification) = {
    collection.insert(notification)
    Future.successful(notification)
  }

  def update(notificationID: UUID, newNotification: Notification): Future[Notification] = {
    collection.update(Json.obj("notificationID" -> notificationID), newNotification)
    Future.successful(newNotification)
  }

  def remove(notificationID: UUID):  Future[List[Notification]] = {
    collection.remove(Json.obj("notificationID" -> notificationID))
    collection.find(Json.obj()).cursor[Notification]().collect[List]()
  }

  def removeByThing(thingID: UUID):  Future[List[Notification]] = {
    collection.remove(Json.obj("thingID" -> thingID))
    collection.find(Json.obj()).cursor[Notification]().collect[List]()
  }

  def removeByThingType(thingTypeID: UUID):  Future[List[Notification]] = {
    collection.remove(Json.obj("thingTypeID" -> thingTypeID))
    collection.find(Json.obj()).cursor[Notification]().collect[List]()
  }

  def removeList(notificationList: List[Notification]):  Future[List[Notification]] = {
    for (notification <- notificationList)
    {
      collection.remove(Json.obj("notificationID" -> notification.notificationID))
    }
    collection.find(Json.obj()).cursor[Notification]().collect[List]()
  }
}
