package models.daos.function

import java.util.UUID

import models.Function

import scala.concurrent.Future
import scala.collection.mutable.ListBuffer

/**
 * Give access to the thing object.
 */
trait FunctionDAO {

  def findAll(): Future[ListBuffer[Function]]

  def find(name: String): Future[Option[Function]]

  def update(name: String, nameNew: Function): Future[Function]

  def save(name: Function): Future[Function]

  def remove(name: String): Future[List[Function]]

}
