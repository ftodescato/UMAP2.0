package models.daos.thing

import java.util.UUID

import models.Thing
import models.DetectionDouble
import models.Measurements

import scala.concurrent.Future

/**
 * Give access to the thing object.
 */
trait ThingDAO {


  def findByName(thingName: String): Future[Option[Thing]]

  def findAll(): Future[List[Thing]]

  def findByCompanyID(companyID: UUID): Future[List[Thing]]

  def findByID(thingID: UUID): Future[Option[Thing]]

  def find(serialNumber: String): Future[Option[Thing]]

  def findListLabel(thing: Thing): List[Double]

  def findMeasurements(thingID: UUID): Future[List[Measurements]]

  def findByThingTypeID(thingTypeID: UUID): Future[List[Thing]]

  def countMeasurements(thingID: UUID): Int
  
  def save(thing: Thing): Future[Thing]

  def update(thingID: UUID, thing2: Thing): Future[Thing]

  def addMeasurements(thingID: UUID, measurements: Measurements): Future[Thing]

  def findListArray(thing: Thing): List[Array[Double]]

  def remove(thingID: UUID): Future[List[Thing]]

  def removeByThingTypeID(thingTypeID: UUID): Future[List[Thing]]
}
