package models.daos.user

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import models.User

import scala.concurrent.Future

/**
 * Give access to the user object.
 */
trait UserDAO {


  def findAll(): Future[List[User]]

  def findByIDCompany(companyID: UUID) : Future[List[User]]

  def find(loginInfo: LoginInfo): Future[Option[User]]

  def findByID(userID: UUID): Future[Option[User]]

  def findByName(userName: String): Future[List[User]]

  def findBySurname(userSurname: String): Future[List[User]]

  def save(user: User): Future[User]

  def update(userID: UUID, user2: User): Future[User]

  def remove(userID: UUID): Future[Boolean]

  def removeByCompany(companyID: UUID): Future[Boolean]

}
