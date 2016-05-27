package controllers.shared.account

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
//import forms.formAccount._
import models._
import play.api.i18n.MessagesApi
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.Action

import scala.concurrent.Future

class AccountController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, JWTAuthenticator])
  extends Silhouette[User, JWTAuthenticator] {



  //  def editProfile = SecuredAction.async { implicit request =>
  //    Future.successful(Ok(views.html.superAdmin.home(request.identity)))
  //  }
  //
  //  def editProfilePost = SecuredAction.async { implicit request =>
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
