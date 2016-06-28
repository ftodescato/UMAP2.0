package models.daos.thingType

import java.util.UUID

import models.ThingType

import scala.concurrent.Future

/**
 * Give access to the ThingType object.
 */
trait ThingTypeDAO {

  def findByName(thingTypeName: String): Future[Option[ThingType]]

  def findAll(): Future[List[ThingType]]

  def findByID(thingTypeID: UUID): Future[Option[ThingType]]

  def findByCompanyID(companyID: UUID): Future[List[ThingType]]

  def save(thingType: ThingType): Future[ThingType]

  def update(thingTypeID: UUID, thingType2: ThingType): Future[ThingType]

  def remove(thingTypeID: UUID): Future[List[ThingType]]
}
