package models.daos.company

import javax.inject.Inject

import play.api.libs.json._

import reactivemongo.api._

import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection._

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import models.Company

import java.util.UUID


class CompanyDAOImpl @Inject() (db : DB) extends CompanyDAO {

  def collection: JSONCollection = db.collection[JSONCollection]("company")


  def findByName(companyName: String): Future[Option[Company]] = {
    collection.find(Json.obj("companyName" -> companyName)).one[Company]
  }
  def findByPIVA(companyPIVA: String): Future[Option[Company]] = {
    collection.find(Json.obj("companyPIVA" -> companyPIVA)).one[Company]
  }

  def findByIDUser(userID: UUID): Future[Option[Company]] ={
    collection.find(Json.obj("userID" -> userID)).one[Company]
  }

  def findAll(): Future[List[Company]] = {
    collection.find(Json.obj()).cursor[Company]().collect[List]()
  }

  def findByID(companyID: UUID) : Future[Option[Company]] = {
    collection.find(Json.obj("companyID" -> companyID)).one[Company]
  }

  def checkExistence(companyIDs: List[UUID]): Future[Boolean] = {
    var exist = true;
    // var aux = true;
    for( companyID <- companyIDs ){
      this.findByID(companyID).flatMap{
        case None =>
          exist = false
          Future.successful(exist)
        case Some(company) =>
          Future.successful(exist)
      }
    }
    Future.successful(exist)
  }

  def save(company: Company) = {
    collection.insert(company)
    Future.successful(company)
  }

  def update(companyID: UUID, company2: Company) = {
    collection.update(Json.obj("companyID" -> companyID),company2)
      Future.successful(company2)
  }

  def remove(companyID: UUID): Future[List[Company]] = {
    collection.remove(Json.obj("companyID" -> companyID))
    collection.find(Json.obj()).cursor[Company]().collect[List]()
  }
}
