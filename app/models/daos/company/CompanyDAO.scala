package models.daos.company

import java.util.UUID

import models.Company

import scala.concurrent.Future

/**
 * Give access to the company object.
 */
trait CompanyDAO {

  def findByName(companyName: String): Future[Option[Company]]

  def findAll(): Future[List[Company]]

  def findByID(companyID: UUID): Future[Option[Company]]

  def findByIDUser(userID: UUID): Future[Option[Company]]

  def save(company: Company): Future[Company]

  def update(companyID: UUID, company2: Company): Future[Company]

  def remove(companyID: UUID):  Future[List[Company]]

}
