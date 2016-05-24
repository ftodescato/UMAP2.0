package models.daos.thingType

import java.util.UUID

import models.ThingType

import scala.collection.mutable
import scala.concurrent.Future

import javax.inject.Inject
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.api._

import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection._

/**
 * Give access to the chart object.
 */
class ThingTypeDAOImpl @Inject() (db : DB) extends ThingTypeDAO {

  def collection: JSONCollection = db.collection[JSONCollection]("thingType")


  def findAll(): Future[List[ThingType]] = {
    collection.find(Json.obj()).cursor[ThingType]().collect[List]()
  }

  /**
   * Finds a thingType by its thingType ID.
   *
   * @param thingTypeID The ID of the thingType to find.
   * @return The found thingType or None if no thingType for the given ID could be found.
   */
  def find(thingTypeID: UUID) : Future[Option[ThingType]] = {
    collection.find(Json.obj("thingTypeID" -> thingTypeID)).one[ThingType]
  }

  /**
   * Saves a thingType.
   *
   * @param thingType The thingType to save.
   * @return The saved thingType.
   */
  def save(thingType: ThingType) = {
    collection.insert(thingType)
    Future.successful(thingType)
  }
}
