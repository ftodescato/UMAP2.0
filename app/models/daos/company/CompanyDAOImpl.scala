package models.daos.company

import java.util.UUID

import models.Company

import scala.collection.mutable
import scala.concurrent.Future

import javax.inject.Inject
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.api._

import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection._

/**
 * Give access to the company object.
 */
class CompanyDAOImpl @Inject() (db : DB) extends CompanyDAO {

  def collection: JSONCollection = db.collection[JSONCollection]("company")

  /**
   * Finds a company by its name.
   *
   * @param companyName The name of the company to find.
   * @return The found company or None if no company for the given name could be found.
   */
  def findByName(companyName: String): Future[Option[Company]] = {

    collection.find(Json.obj("companyName" -> companyName)).one[Company]

  }

  def findAll(): Future[List[Company]] = {
    collection.find(Json.obj()).cursor[Company]().collect[List]()
  }

  /**
   * Finds a company by its company ID.
   *
   * @param companyID The ID of the company to find.
   * @return The found company or None if no company for the given ID could be found.
   */
  def findByID(companyID: UUID) : Future[Option[Company]] = {
    collection.find(Json.obj("companyID" -> companyID)).one[Company]
  }

  /**
   * Saves a company.
   *
   * @param company The company to save.
   * @return The saved company.
   */
  def save(company: Company) = {
    collection.insert(company)
    Future.successful(company)
  }

  def update(companyID: UUID, company2: Company) = {
    collection.update(Json.obj("companyID" -> companyID),company2)
      Future.successful(company2)
  }

  def remove(companyID: UUID) = {
    collection.remove(Json.obj("companyID" -> companyID))
    Future.successful(true)
  }
}
