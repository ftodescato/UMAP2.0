package models.daos.company

import java.util.UUID

import models.Company

import scala.concurrent.Future

/**
 * Give access to the company object.
 */
trait CompanyDAO {

  /**
   * Finds a company by its name.
   *
   * @param companyName The name of the company to find.
   * @return The found company or None if no company for the given name could be found.
   */

  def findByName(companyName: String): Future[Option[Company]]
  def findAll(): Future[List[Company]]

  /**
   * Finds a company by its ID.
   *
   * @param companyID The ID of the company to find.
   * @return The found company or None if no company for the given ID could be found.
   */
  def findByID(companyID: UUID): Future[Option[Company]]

  /**
   * Saves a company.
   *
   * @param company The company to save.
   * @return The saved company.
   */
  def save(company: Company): Future[Company]

  def update(companyID: UUID, company2: Company): Future[Company]

  def remove(companyID: UUID):  Future[Boolean]

}
