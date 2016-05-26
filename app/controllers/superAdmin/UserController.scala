package controllers.superAdmin

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import forms.formUser._
import models.User
import models.services._
import models.daos.user._
import play.api.i18n.{ MessagesApi, Messages }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.Action

import scala.concurrent.Future

/**
 * The sign up controller.
 *
 * @param messagesApi The Play messages API.
 * @param env The Silhouette environment.
 * @param userService The user service implementation.
 * @param authInfoRepository The auth info repository implementation.
 * @param avatarService The avatar service implementation.
 * @param passwordHasher The password hasher implementation.
 */
class UserController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, JWTAuthenticator],
  userService: UserService,
  userDao: UserDAO,
  authInfoRepository: AuthInfoRepository,
  avatarService: AvatarService,
  passwordHasher: PasswordHasher)
  extends Silhouette[User, JWTAuthenticator] {

    def showUsers = Action.async{ implicit request =>
     val users = userDao.findAll()
       users.flatMap{
        users =>
       Future.successful(Ok(Json.toJson(users)))
       }
    }

    def showUserDetails(userID: UUID) = Action.async{ implicit request =>
      val user = userDao.findByID(userID)
        user.flatMap{
         user =>
        Future.successful(Ok(Json.toJson(user)))
        }
    }

    def updateUser = Action.async(parse.json) { implicit request =>
      request.body.validate[EditUserForm.Data].map { data =>
        val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
        userService.retrieve(loginInfo).flatMap {
          case None => Future.successful(BadRequest(Json.obj("message" -> Messages("user.exists"))))
          case Some(user) =>
            val authInfo = passwordHasher.hash(data.password)
            val user2 = User(
              userID = Some(data.userID),
              loginInfo = loginInfo,
              email = Some(data.email),
              role = data.role
            )
            for {
              //user <- userService.save(user.copy(avatarURL = avatar))
              user <- userService.update(userID,user2)
              authInfo <- authInfoRepository.add(loginInfo, authInfo)
              authenticator <- env.authenticatorService.create(loginInfo)
              token <- env.authenticatorService.init(authenticator)
            } yield {
              env.eventBus.publish(SignUpEvent(user, request, request2Messages))
              env.eventBus.publish(LoginEvent(user, request, request2Messages))
              Ok(Json.obj("token" -> token))
            }
        }
      }.recoverTotal {
        case error =>
          Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
      }
   }

  def addUser = Action.async(parse.json) { implicit request =>
    request.body.validate[SignUpForm.Data].map { data =>
      val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
      userDao.find(loginInfo).flatMap {
        case Some(user) =>
          Future.successful(BadRequest(Json.obj("message" -> Messages("user.exists"))))
        case None =>
          val authInfo = passwordHasher.hash(data.password)
          val user = User(
            userID = UUID.randomUUID(),
            loginInfo = loginInfo,
            email = Some(data.email),
            role = data.role
          )
          for {
            //user <- userService.save(user.copy(avatarURL = avatar))
            user <- userDao.save(user)
            authInfo <- authInfoRepository.add(loginInfo, authInfo)
            authenticator <- env.authenticatorService.create(loginInfo)
            token <- env.authenticatorService.init(authenticator)
          } yield {
            env.eventBus.publish(SignUpEvent(user, request, request2Messages))
            env.eventBus.publish(LoginEvent(user, request, request2Messages))
            Ok(Json.obj("token" -> token))
          }
      }
    }.recoverTotal {
      case error =>
        Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
    }
  }
}
