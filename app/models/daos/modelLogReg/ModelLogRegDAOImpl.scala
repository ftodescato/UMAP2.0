package models.daos.modelLogReg

import java.util.UUID

import models.LogRegModel

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
class ModelLogRegDAOImpl @Inject() (db : DB) extends ModelLogRegDAO {

  def collection: JSONCollection = db.collection[JSONCollection]("modelLogReg")


  def findByThingID(thingID: UUID): Future[Option[LogRegModel]] = {
    collection.find(Json.obj("thingID" -> thingID)).one[LogRegModel]
  }


  def save(logRegModelID: LogRegModel): Future[LogRegModel] = {
    collection.insert(logRegModelID)
    Future.successful(logRegModelID)
  }

  def update(logRegModelID: UUID, newModelLogReg: LogRegModel): Future[LogRegModel] = {
    collection.update(Json.obj("logRegModelID" -> logRegModelID), newModelLogReg)
    Future.successful(newModelLogReg)
  }


  def remove(logRegModelID: UUID): Future[List[LogRegModel]] = {
    collection.remove(Json.obj("logRegModelID" -> logRegModelID))
    collection.find(Json.obj()).cursor[LogRegModel]().collect[List]()

  }
}
