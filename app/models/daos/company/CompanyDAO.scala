package models.daos.company

import java.util.UUID

import models.Company

import scala.concurrent.Future

/**
 * Give access to the company object.
 */
trait CompanyDAO {

  /**
   * Finds a Company by its name.
   *
   * @param companyName The name of the company to find.
   * @return The found company or None if no chart for the given name could be found.
   */
  def findByName(companyName: String): Future[Option[Company]]

  /**
   * Finds all Companies.
   *
   * @return The all company in database.
   */
  def findAll(): Future[List[Company]]

  /**
   * Finds a Company by its ID.
   *
   * @param companyID The ID of the company to find.
   * @return The found company or None if no chart for the given name could be found.
   */
  def findByID(companyID: UUID): Future[Option[Company]]

  /**
   * Finds a Company by one of its userID.
   *
   * @param userID The ID of user that is in the company to find.
   * @return The found company or None if no chart for the given name could be found.
   */
  def findByIDUser(userID: UUID): Future[Option[Company]]

  /**
   * Boolean of presence of a Company.
   *
   * @param companyIDs The list company to check if it's in db.
   * @return The boolean.
   */
  def checkExistence(companyIDs: List[UUID]): Future[Boolean]

  /**
   * Saves a Company.
   *
   * @param company The company to save.
   * @return The save company.
   */
  def save(company: Company): Future[Company]

  /**
   * Update a Company.
   *
   * @param companyID The company to update.
   * @return The company updated.
   */
  def update(companyID: UUID, company2: Company): Future[Company]

  /**
   * Remove a Company.
   *
   * @param companyID The company to remove.
   * @return The list of company in db.
   */
  def remove(companyID: UUID):  Future[List[Company]]

}
