package models.daos.thing

import java.util.UUID

import models.Thing
import models.Measurements
import models.DetectionDouble

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

  // def findMeasuremets(thing: Thing, measurementsID: UUID) : Future[Measurements]

  def findListLabel(thing: Thing): List[Double]

  def save(thing: Thing): Future[Thing]

  def update(thingID: UUID, thing2: Thing): Future[Thing]

  def updateMeasurements(thingID: UUID, measurements: Measurements): Future[Thing]
  def findListArray(thing: Thing): List[Array[Double]]

  // def updateDectentionDouble(thingID:UUID, measurements: Measurements, detectionDouble: DetectionDouble): Future[Thing]

  def remove(thingID: UUID): Future[List[Thing]]

  def removeByThingTypeID(thingTypeID: UUID): Future[List[Thing]]
}
