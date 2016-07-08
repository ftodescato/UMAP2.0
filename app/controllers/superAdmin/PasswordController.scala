package controllers.superAdmin

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import forms.company._
import forms.password._
import models._
import play.api.libs.mailer._

import models.PersistentPasswordInfo
import models.User
import models.services._
import models.daos.user.UserDAO
import models.daos.password.PasswordInfoDAO
import play.api.i18n.{ MessagesApi, Messages }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.Action

//import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
//import com.mohiva.play.silhouette.api.services.AvatarService
//import com.mohiva.play.silhouette.api.util.PasswordHasher
//import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import scala.concurrent.Future


class PasswordController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, JWTAuthenticator],
  userService: UserService,
  userDao: UserDAO,
  passwordInfoDao: PasswordInfoDAO,
  authInfoRepository: AuthInfoRepository,
  avatarService: AvatarService,
  passwordHasher: PasswordHasher)
  extends Silhouette[User, JWTAuthenticator] {


    def updatePassword(userID: UUID) = SecuredAction(WithServices(Array("superAdmin","admin","user"), true)).async(parse.json) { implicit request =>
      request.body.validate[EditPassword.Data].map { data =>
        userDao.findByID(userID).flatMap {
          case None => Future.successful(BadRequest(Json.obj("message" -> Messages("user.notComplete"))))
          case Some(user) =>
            val loginInfo = LoginInfo(CredentialsProvider.ID, user.email)
            passwordInfoDao.find(loginInfo).flatMap{
              case None =>
                Future.successful(BadRequest(Json.obj("message" -> Messages("mail.notExists"))))
              case Some(psw) =>
              var authInfo = passwordHasher.hash(data.newPassword)
              userDao.confirmedMail(user)
                for{
                  authInfo <- passwordInfoDao.update(loginInfo, authInfo)
                }yield {
                  Ok(Json.obj("token" -> "ok"))
                 }
            }
      }
    }.recoverTotal {
      case error =>
        Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
      }
  }
}
