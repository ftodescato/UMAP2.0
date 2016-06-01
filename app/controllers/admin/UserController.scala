package controllers.admin

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
import models.Company
import models.services._
import models.daos.user._
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
  authInfoRepository: AuthInfoRepository,
  avatarService: AvatarService,
  passwordHasher: PasswordHasher)
  extends Silhouette[User, JWTAuthenticator] {

  def showUsers(companyID: UUID) = Action.async{ implicit request =>
   val users = userDao.findByIDCompany(companyID)
   users.flatMap{
    users =>
     Future.successful(Ok(Json.toJson(users)))
   }
  }

  //lato fronted bisogna fare la ricerca findByID solo sugli utenti della stessa company
  def showUsersByName(userName: String) = Action.async{ implicit request =>
    val users = userDao.findByName(userName)
    users.flatMap{
     users =>
      Future.successful(Ok(Json.toJson(users)))
    }
  }

  //lato fronted bisogna fare la ricerca findByID solo sugli utenti della stessa company
  def showUsersBySurname(userSurname: String) = Action.async{ implicit request =>
    val users = userDao.findBySurname(userSurname)
    users.flatMap{
     users =>
      Future.successful(Ok(Json.toJson(users)))
    }
  }

  //lato fronted bisogna fare la ricerca findByID solo sugli utenti della stessa company
 def showUserDetails(userID: UUID) = Action.async{ implicit request =>
      val user = userDao.findByID(userID)
        user.flatMap{
         user =>
        Future.successful(Ok(Json.toJson(user)))
        }
    }

    //lato fronted bisogna fare la ricerca findByID solo sugli utenti della stessa company
    def delete(userID: UUID) = Action.async{ implicit request =>
      userDao.findByID(userID).flatMap{
          case None => Future.successful(BadRequest(Json.obj("message" -> "User non trovato")))
          case Some (user) =>
            for{
              user <- userDao.remove(userID)
            }yield {
                //env.eventBus.publish(SignUpEvent(user, request, request2Messages))
                //env.eventBus.publish(LoginEvent(user, request, request2Messages))
                Ok(Json.obj("ok" -> "ok"))
              }
            }
     }

     //lato fronted bisogna fare aggiungere la company dell'admin (nascosta nella form)
    def updateUser(userID: UUID) = Action.async(parse.json) { implicit request =>
      request.body.validate[EditUser.Data].map { data =>
        val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
        userService.retrieve(loginInfo).flatMap {
          case None => Future.successful(BadRequest(Json.obj("message" -> Messages("user.notComplete"))))
          case Some(user) =>
            val companyInfo = companyDao.findByIDUser(userID)
            val authInfo = passwordHasher.hash(data.password)
            val user2 = User(
              userID = user.userID,
              name = data.name,
              surname = data.surname,
              loginInfo = loginInfo,
              email = Some(data.email),
              company = data.company,
              role = data.role
            )
            for {
              //user <- userService.save(user.copy(avatarURL = avatar))
              user <- userDao.update(userID,user2)
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

  def addUser(companyID: UUID) = Action.async(parse.json) { implicit request =>
    request.body.validate[SignUp.Data].map { data =>
      val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
      userDao.find(loginInfo).flatMap {
        case Some(user) =>
          Future.successful(BadRequest(Json.obj("message" -> Messages("user.exists"))))
            case None =>
            val authInfo = passwordHasher.hash(data.password)
            val user = User(
              userID = UUID.randomUUID(),
              name = data.name,
              surname = data.surname,
              loginInfo = loginInfo,
              email = Some(data.email),
              company = companyID,
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
