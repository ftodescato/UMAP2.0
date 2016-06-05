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

  def findByName(thingName: String): Future[Option[Thing]] = {
    collection.find(Json.obj("name" -> thingName)).one[Thing]
  }


  def findAll(): Future[List[Thing]] = {
    collection.find(Json.obj()).cursor[Thing]().collect[List]()
  }

  def findByCompany(companyID: UUID): Future[List[Thing]] = {
    collection.find(Json.obj("companyID" -> companyID)).cursor[Thing]().collect[List]()
  }

  def findByID(thingID: UUID) : Future[Option[Thing]] = {
    collection.find(Json.obj("thingID" -> thingID)).one[Thing]
  }

  def find(serialNumber: String) : Future[Option[Thing]] = {
    collection.find(Json.obj("serialNumber" -> serialNumber)).one[Thing]
  }

  def save(thing: Thing): Future[Thing] = {
    collection.insert(thing)
    Future.successful(thing)
  }

  def update(thingID: UUID, thing2: Thing): Future[Thing] = {
    collection.update(Json.obj("thingID" -> thingID), thing2)
    Future.successful(thing2)
  }

  def remove(thingID: UUID): Future[List[Thing]] = {
    collection.remove(Json.obj("thingID" -> thingID))
    collection.find(Json.obj()).cursor[Thing]().collect[List]()
  }

  def removeByThingTypeID(thingTypeID: UUID): Future[List[Thing]] = {
    collection.remove(Json.obj("thingTypeID" -> thingTypeID))
    collection.find(Json.obj()).cursor[Thing]().collect[List]()
  }
}
