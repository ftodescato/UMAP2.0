package models.daos.thingType

import java.util.UUID

import models.ThingType

import scala.concurrent.Future

/**
 * Give access to the ThingType object.
 */
trait ThingTypeDAO {


  def findAll(): Future[List[ThingType]]

  /**
   * Finds a thingType by its thingType ID.
   *
   * @param thingTypeID The ID of the ThingType to find.
   * @return The found ThingType or None if no ThingType for the given ID could be found.
   */
  def find(thingTypeID: UUID): Future[Option[ThingType]]

  /**
   * Saves a thingType.
   *
   * @param thingType The thingType to save.
   * @return The saved thingType.
   */
  def save(thingType: ThingType): Future[ThingType]
}
