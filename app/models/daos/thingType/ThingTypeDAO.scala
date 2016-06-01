package models.daos.thingType

import java.util.UUID

import models.ThingType

import scala.concurrent.Future

/**
 * Give access to the ThingType object.
 */
trait ThingTypeDAO {


  def findAll(): Future[List[ThingType]]

  def findByID(thingTypeID: UUID): Future[Option[ThingType]]

  def save(thingType: ThingType): Future[ThingType]

  def update(thingTypeID: UUID, thingType2: ThingType)

  def remove(thingTypeID: UUID): Future[List[ThingType]]
}
