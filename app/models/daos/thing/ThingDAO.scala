package models.daos.thing

import java.util.UUID

import models.Thing

import scala.concurrent.Future

/**
 * Give access to the thing object.
 */
trait ThingDAO {

  /**
   * Finds a thing by its name.
   *
   * @param thingName The name of the thing to find.
   * @return The found thing or None if no thing for the given name could be found.
   */

  def findByName(thingName: String): Future[Option[Thing]]
  def findAll(): Future[List[Thing]]

  /**
   * Finds a thing by companyID.
   *
   * @param companyID The ID of the company bound to the thing to find.
   * @return The found thing or None if no thing for the given companyID could be found.
   */

  def findByCompany(companyID: UUID): Future[Option[Thing]]

  /**
   * Finds a thing by its thing ID.
   *
   * @param thingID The ID of the thing to find.
   * @return The found thing or None if no thing for the given ID could be found.
   */
  def findByID(thingID: UUID): Future[Option[Thing]]

  /**
   * Finds a thing by its serial number.
   *
   * @param serialNumber The serial number of the thing to find.
   * @return The found thing or None if no thing for the given serial numebr could be found.
   */
  def find(serialNumber: String): Future[Option[Thing]]

  /**
   * Saves a thing.
   *
   * @param thing The user to save.
   * @return The saved thing.
   */
  def save(thing: Thing): Future[Thing]
}
