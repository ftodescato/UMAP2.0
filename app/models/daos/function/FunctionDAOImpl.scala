package models.daos.function

import java.util.UUID

import models.Function

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.ListBuffer

import javax.inject.Inject
import play.api.libs.json._

import reactivemongo.api._

import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection._

/**
 * Give access to the chart object.
 */
class FunctionDAOImpl @Inject() (db : DB) extends FunctionDAO {

  def collection: JSONCollection = db.collection[JSONCollection]("function")

  def findAll(): Future[ListBuffer[Function]] = {
    collection.find(Json.obj()).cursor[Function]().collect[ListBuffer]()
  }


  def find(name: String): Future[Option[Function]] = {
    collection.find(Json.obj("name" -> name)).one[Function]
  }

  def update(name: String, newFunction: Function): Future[Function] ={
    collection.update(Json.obj("name" -> name), newFunction)
    Future.successful(newFunction)
  }

  def save(function: Function): Future[Function] = {
    collection.insert(function)
    Future.successful(function)
  }

  def remove(name: String): Future[List[Function]] = {
    collection.remove(Json.obj("name" -> name))
    collection.find(Json.obj()).cursor[Function]().collect[List]()
  }
}
