package models.services

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import models.User
import models.daos.user._
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future


class UserServiceImpl @Inject() (userDAO: UserDAO) extends UserService {


  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = userDAO.find(loginInfo)

  def save(user: User) = userDAO.save(user)

}
