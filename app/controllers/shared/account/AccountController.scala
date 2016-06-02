package controllers.shared.account


import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import forms.user._
import forms.password._
import models.User
import models.services._
import models.daos.user._
import models.daos.password._
import models.daos.company._
import play.api.i18n.{ MessagesApi, Messages }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.Action

import scala.concurrent.Future

class AccountController @Inject() (
  val messagesApi: MessagesApi,
  userDao: UserDAO,
  companyDao: CompanyDAO,
  passwordInfoDao: PasswordInfoDAO,
  passwordHasher: PasswordHasher,
  val env: Environment[User, JWTAuthenticator])
  extends Silhouette[User, JWTAuthenticator] {



   def getInfo = SecuredAction.async { implicit request =>
     Future.successful(Ok(Json.toJson(request.identity)))
   }

   def updateAccount = SecuredAction.async(parse.json) { implicit request =>
       request.body.validate[EditUser.Data].map { data =>
         userDao.findByID(request.identity.userID).flatMap {
           case None => Future.successful(BadRequest(Json.obj("message" -> Messages("user.notComplete"))))
           case Some(user) =>
           val loginInfo = LoginInfo(CredentialsProvider.ID, user.email)
           val loginInfoNew = LoginInfo(CredentialsProvider.ID, data.email)
           passwordInfoDao.find(loginInfo).flatMap{
             case None =>
               Future.successful(BadRequest(Json.obj("message" -> Messages("mail.notExists"))))
             case Some(psw) =>
               val authInfo = psw
               for{
                 authInfo <- passwordInfoDao.updateNewLoginInfo(loginInfo, loginInfoNew, authInfo)
               }yield {
                 Ok(Json.obj("token" -> "ok"))
                }
           }
             val companyInfo = companyDao.findByIDUser(request.identity.userID)
             val user2 = User(
               userID = user.userID,
               name = data.name,
               surname = data.surname,
               loginInfo = loginInfoNew,
               email = data.email,
               company = user.company,
               role = user.role
             )
             for {
               user <- userDao.update(request.identity.userID,user2)
               authenticator <- env.authenticatorService.create(loginInfo)
               //token <- env.authenticatorService.init(authenticator)
             } yield {
               Ok(Json.obj("token" -> "ok"))
             }
         }
       }.recoverTotal {
         case error =>
           Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
     }
   }
   def updatePassword = SecuredAction.async(parse.json) { implicit request =>
     request.body.validate[EditPassword.Data].map { data =>
       userDao.findByID(request.identity.userID).flatMap {
         case None => Future.successful(BadRequest(Json.obj("message" -> Messages("user.notComplete"))))
         case Some(user) =>
           val loginInfo = LoginInfo(CredentialsProvider.ID, user.email)
           passwordInfoDao.find(loginInfo).flatMap{
             case None =>
               Future.successful(BadRequest(Json.obj("message" -> Messages("mail.notExists"))))
             case Some(psw) =>
             var authInfo = passwordHasher.hash(data.newPassword)
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
  //
  //  def editMyCompany = SecuredAction.async { implicit request =>
  //    var company = request.identity
  //    Future.successful(Ok(views.html.superAdmin.home(request.identity)))
  //  }
  //
  //  def editPassword = SecuredAction.async { implicit request =>
  //    Future.successful(Ok(views.html.superAdmin.home(request.identity)))
  //  }
  //
  //  def editPasswordPost = SecuredAction.async { implicit request =>
  //    Future.successful(Ok(views.html.superAdmin.home(request.identity)))
  //  }
  //
  //  def editEmail = SecuredAction.async { implicit request =>
  //    Future.successful(Ok(views.html.superAdmin.home(request.identity)))
  //  }
  //
  //  def editEmailPost = SecuredAction.async { implicit request =>
  //    Future.successful(Ok(views.html.superAdmin.home(request.identity)))
  //  }
}
