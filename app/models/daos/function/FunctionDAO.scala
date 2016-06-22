package models.daos.function

import java.util.UUID

import models.Function

import scala.concurrent.Future
import scala.collection.mutable.ListBuffer

/**
 * Give access to the thing object.
 */
trait FunctionDAO {

  /**
   * Finds all Companies.
   *
   * @return The all function in database.
   */
  def findAll(): Future[ListBuffer[Function]]

  /**
   * Finds a Function by its name.
   *
   * @param name The name of the function to find.
   * @return The found function or None if no function for the given name could be found.
   */
  def find(name: String): Future[Option[Function]]

  /**
   * Update a Function.
   *
   * @param name The function to update.
   * @return The function updated.
   */
  def update(name: String, nameNew: Function): Future[Function]

  /**
   * Saves a Function.
   *
   * @param name The function to save.
   * @return The function company.
   */
  def save(name: Function): Future[Function]

  /**
   * Remove a Function.
   *
   * @param name The function to remove.
   * @return The list of function in db.
   */
  def remove(name: String): Future[List[Function]]

}
