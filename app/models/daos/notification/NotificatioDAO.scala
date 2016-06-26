package models.daos.notification

import java.util.UUID

import models.Notification

import scala.concurrent.Future

/**
 * Give access to the chart object.
 */
trait NotificationDAO {


  def findNotificationOfThingType(thingTypeID: UUID): Future[Option[Notification]]

  def findAll(): Future[List[Notification]]

  def find(notificationID: UUID): Future[Option[Notification]]

  def save(notification: Notification): Future[Notification]

  def update(notificationID: UUID, notification2: Notification)

  def remove(notificationID: UUID): Future[List[Notification]]
}
