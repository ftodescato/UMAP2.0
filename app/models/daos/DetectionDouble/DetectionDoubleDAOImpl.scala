package models.daos.detectionDouble

import models.Thing
import models.Measurements
import models.DetectionDouble
import models.daos.detectionDouble

import scala.collection.mutable
import scala.concurrent.Future

import javax.inject.Inject
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.api._

import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection._

class DetectionDoubleDAOImpl @Inject() (db : DB) extends DetectionDoubleDAO {

  def collection: JSONCollection = db.collection[JSONCollection]("detectionDouble")

  def add(detectionDouble: DetectionDouble): Future[DetectionDouble] = {
    collection.insert(detectionDouble)
    Future.successful(detectionDouble)
  }

}
