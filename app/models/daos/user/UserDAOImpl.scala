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
  def findByEmail(email: String): Future[Option[User]] ={
    collection.find(Json.obj( "email" -> email )).one[User]
  }
  def findSecretString(user: User): Future[String] ={
    Future.successful(user.secretString)
  }


  def find(loginInfo: LoginInfo) : Future[Option[User]] = {
    collection.find(Json.obj( "loginInfo" -> loginInfo )).one[User]
  }

  def findByID(userID: UUID) : Future[Option[User]] = {
    collection.find(Json.obj("userID" -> userID)).one[User]
  }

  def findByName(userName: String) :  Future[List[User]] = {
    collection.find(Json.obj("name" -> userName)).cursor[User]().collect[List]()
  }

  def findBySurname(userSurname: String) :  Future[List[User]] = {
    collection.find(Json.obj("surname" -> userSurname)).cursor[User]().collect[List]()
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

  def remove(userID: UUID): Future[List[User]] = {
    collection.remove(Json.obj("userID" -> userID))
    collection.find(Json.obj()).cursor[User]().collect[List]()
  }

  def removeByEmail(email: String): Future[List[User]] = {
    collection.remove(Json.obj("email" -> email))
    collection.find(Json.obj()).cursor[User]().collect[List]()
  }

  def removeByCompany(companyID: UUID): Future[List[User]] = {
    collection.remove(Json.obj("company" -> companyID))
    collection.find(Json.obj()).cursor[User]().collect[List]()
  }

  def confirmedMail(user: User): Future[User] = {

      val user2 = User(
        userID = user.userID,
        name = user.name,
        surname = user.surname,
        loginInfo = user.loginInfo,
        email = user.email,
        company = user.company,
        mailConfirmed = true,
        token = "vuoto",
        role = user.role,
        secretString = user.secretString
      )
      collection.update(Json.obj("userID" -> user.userID), user2)
      Future.successful(user2)
  }

}
