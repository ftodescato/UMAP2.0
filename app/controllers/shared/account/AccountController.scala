package controllers.shared.account

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import forms._
import models._
import play.api.i18n.MessagesApi
import play.api.Logger._

import scala.concurrent.Future

/**
 * The basic application controller.
 *
 * @param messagesApi The Play messages API.
 * @param env The Silhouette environment.
 * @param socialProviderRegistry The social provider registry.
 */
class AccountController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, CookieAuthenticator])
  extends Silhouette[User, CookieAuthenticator] {

  /**
   * Handles the index action.
   *
   * @return The result to display.
   */
   def editProfile = SecuredAction.async { implicit request =>
     Future.successful(Ok(views.html.superAdmin.home(request.identity)))
   }

   def editProfilePost = SecuredAction.async { implicit request =>
     Future.successful(Ok(views.html.superAdmin.home(request.identity)))
   }

   def editPassword = SecuredAction.async { implicit request =>
     Future.successful(Ok(views.html.superAdmin.home(request.identity)))
   }

   def editPasswordPost = SecuredAction.async { implicit request =>
     Future.successful(Ok(views.html.superAdmin.home(request.identity)))
   }

   def editEmail = SecuredAction.async { implicit request =>
     Future.successful(Ok(views.html.superAdmin.home(request.identity)))
   }

   def editEmailPost = SecuredAction.async { implicit request =>
     Future.successful(Ok(views.html.superAdmin.home(request.identity)))
   }
}
