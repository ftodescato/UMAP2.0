package models.daos.thing

import java.util.UUID

import models.Thing

import scala.collection.mutable
import scala.concurrent.Future

import javax.inject.Inject
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.api._

import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection._

/**
 * Give access to the thing object.
 */
class ThingDAOImpl @Inject() (db : DB) extends ThingDAO {

  def collection: JSONCollection = db.collection[JSONCollection]("thing")

  /**
   * Finds a thing by its name.
   *
   * @param thingName The name of the thing to find.
   * @return The found thing or None if no thing for the given name could be found.
   */

  def findByName(thingName: String): Future[Option[Thing]] = {
    collection.find(Json.obj("thingName" -> thingName)).one[Thing]
  }

  def findAll(): Future[List[Thing]] = {
    collection.find(Json.obj()).cursor[Thing]().collect[List]()
  }

  /**
   * Finds a thing by companyID.
   *
   * @param companyID The ID of the company bound to the thing to find.
   * @return The found thing or None if no thing for the given companyID could be found.
   */

  def findByCompany(companyID: UUID): Future[Option[Thing]] = {
    collection.find(Json.obj("companyID" -> companyID)).one[Thing]
  }

  /**
   * Finds a thing by its thing ID.
   *
   * @param thingID The ID of the thing to find.
   * @return The found thing or None if no thing for the given ID could be found.
   */
  def findByID(thingID: UUID) : Future[Option[Thing]] = {
    collection.find(Json.obj("thingID" -> thingID)).one[Thing]
  }

  /**
   * Finds a thing by its thing ID.
   *
   * @param serialNumber The serial number of the thing to find.
   * @return The found thing or None if no thing for the given serial number could be found.
   */
  def find(serialNumber: String) : Future[Option[Thing]] = {
    collection.find(Json.obj("serialNumber" -> serialNumber)).one[Thing]
  }

  /**
   * Saves a thing.
   *
   * @param thing The thing to save.
   * @return The saved thing.
   */
  def save(thing: Thing) = {
    collection.insert(thing)
    Future.successful(thing)
  }
}
