package models.daos.user

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import models.User

import scala.concurrent.Future


trait UserDAO {


  def findAll(): Future[List[User]]

  def findByIDCompany(companyID: UUID): Future[List[User]]

  def findAdminByCompanyID(companyID: UUID): Future[List[User]]
  
  def find(loginInfo: LoginInfo): Future[Option[User]]

  def findByID(userID: UUID): Future[Option[User]]

  def findByEmail(email: String): Future[Option[User]]

  def findSecretString(user: User): Future[String]

  def findByName(userName: String): Future[List[User]]

  def findBySurname(userSurname: String): Future[List[User]]

  def save(user: User): Future[User]

  def update(userID: UUID, user2: User): Future[User]

  def remove(userID: UUID): Future[List[User]]

  def removeByEmail(email: String): Future[List[User]]

  def removeByCompany(companyID: UUID): Future[List[User]]

def confirmedMail(user: User): Future[User]
}
