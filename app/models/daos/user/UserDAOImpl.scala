package models.daos.user

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import models.User

import scala.collection.mutable
import scala.concurrent.Future

import javax.inject.Inject
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.api._

import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection._

/**
  * Give access to the user object.
  */
class UserDAOImpl @Inject() (db : DB) extends UserDAO {

  def collection: JSONCollection = db.collection[JSONCollection]("user")


  def findAll(): Future[List[User]] = {
    collection.find(Json.obj()).cursor[User]().collect[List]()
  }

  def findByIDCompany(companyID: UUID) : Future[List[User]] = {
    collection.find(Json.obj("company" -> companyID)).cursor[User]().collect[List]()
  }

  def find(loginInfo: LoginInfo) : Future[Option[User]] = {
    collection.find(Json.obj( "loginInfo" -> loginInfo )).one[User]
  }

  def findByID(userID: UUID) : Future[Option[User]] = {
    collection.find(Json.obj("userID" -> userID)).one[User]
  }

  def save(user: User) = {
    collection.update(Json.obj("userID" -> user.userID),
      user,
      upsert = true)
    Future.successful(user)
  }

  def update(userID: UUID, user2: User) = {
    collection.update(Json.obj("userID" -> userID),user2)
    Future.successful(user2)
  }

  def remove(userID: UUID) = {
    collection.remove(Json.obj("userID" -> userID))
    Future.successful(true)
  }

  def removeByCompany(companyID: UUID) = {
    collection.remove(Json.obj("company" -> companyID))
    Future.successful(true)
  }
}
