package models.daos.notification

import java.util.UUID

import models.Notification

import scala.concurrent.Future

/**
 * Give access to the chart object.
 */
trait NotificationDAO {


  def findNotificationOfThingType(thingTypeID: UUID): Future[List[Notification]]

  def findNotificationOfThing(thingID: UUID): Future[List[Notification]]

  def findAll(): Future[List[Notification]]

  def findByID(notificationID: UUID): Future[Option[Notification]]

  def save(notification: Notification): Future[Notification]

  def update(notificationID: UUID, notification2: Notification) : Future[Notification]

  def remove(notificationID: UUID): Future[List[Notification]]

  def removeByThing(thingID: UUID):  Future[List[Notification]]

  def removeByThingType(thingTypeID: UUID):  Future[List[Notification]]

  def removeList(notificationList: List[Notification]): Future[List[Notification]]

}
