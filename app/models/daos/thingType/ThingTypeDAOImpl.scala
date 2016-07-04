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

  def findByName(thingTypeName: String): Future[Option[ThingType]] = {
    collection.find(Json.obj("thingTypeName" -> thingTypeName)).one[ThingType]
  }

  def findByCompanyID(companyID: UUID): Future[List[ThingType]] = {
    collection.find(Json.obj("companyID"-> companyID)).cursor[ThingType]().collect[List]()
  }

  def findByID(thingTypeID: UUID): Future[Option[ThingType]] = {
    collection.find(Json.obj("thingTypeID" -> thingTypeID)).one[ThingType]
  }

  def save(thingType: ThingType): Future[ThingType] = {
    collection.insert(thingType)
    Future.successful(thingType)
  }

  def update(thingTypeID: UUID, thingType2: ThingType) = {
    collection.update(Json.obj("thingTypeID" -> thingTypeID), thingType2)
    Future.successful(thingType2)

  }

  def remove(thingTypeID: UUID): Future[List[ThingType]] = {
    collection.remove(Json.obj("thingTypeID" -> thingTypeID))
    collection.find(Json.obj()).cursor[ThingType]().collect[List]()
  }
}
