package models.daos.thing

import java.util.UUID

import models.Thing

import scala.concurrent.Future

/**
 * Give access to the thing object.
 */
trait ThingDAO {


  def findByName(thingName: String): Future[Option[Thing]]

  def findAll(): Future[List[Thing]]

  def findByCompany(companyID: UUID): Future[Option[Thing]]

  def findByID(thingID: UUID): Future[Option[Thing]]

  def find(serialNumber: String): Future[Option[Thing]]

  def save(thing: Thing): Future[Thing]

  def update(thingID: UUID, thing2: Thing): Future[Thing]

  def remove(thingID: UUID): Future[List[Thing]]
}
