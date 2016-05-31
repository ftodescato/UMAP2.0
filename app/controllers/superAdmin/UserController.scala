package controllers.superAdmin

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import forms.user._
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


class UserController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, JWTAuthenticator],
  userService: UserService,
  userDao: UserDAO,
  companyDao: CompanyDAO,
  passwordInfoDao: PasswordInfoDAO,
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


    def delete(userID: UUID) = Action.async{ implicit request =>
        userDao.findByID(userID).flatMap{
          case None => Future.successful(BadRequest(Json.obj("message" -> "User non trovato")))
          case Some (user) =>
          val loginInfo = LoginInfo(CredentialsProvider.ID, user.email)
            for{
              user <- userDao.remove(userID)
              authInfo <- passwordInfoDao.remove(loginInfo)
            }yield {
                //env.eventBus.publish(SignUpEvent(user, request, request2Messages))
                //env.eventBus.publish(LoginEvent(user, request, request2Messages))
                Ok(Json.obj("ok" -> "ok"))
              }
            }

     }

    def updateUser(userID: UUID) = Action.async(parse.json) { implicit request =>
      request.body.validate[EditUser.Data].map { data =>
        userDao.findByID(userID).flatMap {
          case None => Future.successful(BadRequest(Json.obj("message" -> Messages("user.notComplete"))))
          case Some(user) =>
          val loginInfo = LoginInfo(CredentialsProvider.ID, user.email)
          val companyInfo = data.company
          companyDao.findByID(companyInfo).flatMap{
          case Some(companyToAssign) =>
            val authInfo = passwordHasher.hash(data.password)
            val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
            val user2 = User(
              userID = user.userID,
              name = data.name,
              surname = data.surname,
              loginInfo = loginInfo,
              email = data.email,
              company = data.company,
              role = data.role
            )
            for {
              //user <- userService.save(user.copy(avatarURL = avatar))
              user <- userDao.update(userID,user2)
              authInfo <- passwordInfoDao.update(loginInfo,authInfo)
              authenticator <- env.authenticatorService.create(loginInfo)
              token <- env.authenticatorService.init(authenticator)
            } yield {
            //  env.eventBus.publish(SignUpEvent(user, request, request2Messages))
            //  env.eventBus.publish(LoginEvent(user, request, request2Messages))
              Ok(Json.obj("token" -> data.password))
            }
            case None =>
              Future.successful(BadRequest(Json.obj("message" -> Messages("company.notExists"))))
          }
        }
      }.recoverTotal {
        case error =>
          Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
    }
}

  def addUser = Action.async(parse.json) { implicit request =>
    request.body.validate[SignUp.Data].map { data =>
      var loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
      userDao.find(loginInfo).flatMap {
        case Some(user) =>
          Future.successful(BadRequest(Json.obj("message" -> Messages("user.exists"))))
            case None =>
            val companyInfo = data.company
            companyDao.findByID(companyInfo).flatMap{
            case Some(companyToAssign) =>
            var authInfo = passwordHasher.hash(data.password)
            val user = User(
              userID = UUID.randomUUID(),
              name = data.name,
              surname = data.surname,
              loginInfo = loginInfo,
              email = data.email,
              company = data.company,
              role = data.role
            )
            for {
              //user <- userService.save(user.copy(avatarURL = avatar))
              authInfo <- authInfoRepository.add(loginInfo, authInfo)
              authenticator <- env.authenticatorService.create(loginInfo)
              user <- userDao.save(user)
            //  token <- env.authenticatorService.init(authenticator)
            } yield {
            //  env.eventBus.publish(SignUpEvent(user, request, request2Messages))
            //  env.eventBus.publish(LoginEvent(user, request, request2Messages))
              Ok(Json.obj("token" -> data.password))
            }
            case None =>
              Future.successful(BadRequest(Json.obj("message" -> Messages("company.notExists"))))
          }
      }
  }.recoverTotal {
      case error =>
        Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
    }
  }
}
