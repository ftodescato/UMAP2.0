package models.daos.notification

import java.util.UUID

import models.Notification

import scala.concurrent.Future

/**
 * Give access to the chart object.
 */
trait NotificationDAO {

  /**
   * Finds a notification by model ID.
   *
   * @param thingTypeID The ID of the model to find.
   * @return The found notification or None if no notification for the given modelID could be found.
   */
  def findNotificationOfThingType(thingTypeID: UUID): Future[Option[Notification]]
  def findAll(): Future[List[Notification]]

  /**
   * Finds a notification by its notification ID.
   *
   * @param notificationID The ID of the notification to find.
   * @return The found notification or None if no notification for the given ID could be found.
   */
  def find(notificationID: UUID): Future[Option[Notification]]

  /**
   * Saves a notification.
   *
   * @param chart The notification to save.
   * @return The saved notification.
   */
  def save(notification: Notification): Future[Notification]
}
