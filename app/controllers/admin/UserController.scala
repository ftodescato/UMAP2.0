package controllers.admin

import java.util.UUID
import javax.inject.Inject
import play.api.libs.mailer._

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException

import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import forms.user._
import forms.modelAnalyticalData._

import models._
import models.services._
import models.daos.user._
import models.daos.chart._
import models.daos.password._
import models.daos.company._
import models.User
import models.Company
import models.Chart

import play.api.i18n.{ MessagesApi, Messages }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.Action

import scala.concurrent.Future
import scala.collection.mutable.ListBuffer



class UserController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, JWTAuthenticator],
  mailer: MailerClient,
  userService: UserService,
  chartDao: ChartDAO,
  userDao: UserDAO,
  companyDao: CompanyDAO,
  passwordInfoDao: PasswordInfoDAO,
  authInfoRepository: AuthInfoRepository,
  avatarService: AvatarService,
  passwordHasher: PasswordHasher)
  extends Silhouette[User, JWTAuthenticator] {


  def showUsers = SecuredAction(WithServices("admin", true)).async{ implicit request =>
   val users = userDao.findByIDCompany(request.identity.company)
   users.flatMap{
    users =>
     Future.successful(Ok(Json.toJson(users)))
   }
  }

  //lato fronted bisogna fare la ricerca findByID solo sugli utenti della stessa company
  def showUsersByName(userName: String) = SecuredAction(WithServices("admin", true)).async{ implicit request =>
    val users = userDao.findByName(userName)
    users.flatMap{
     users =>
      Future.successful(Ok(Json.toJson(users)))
    }
  }

  //lato fronted bisogna fare la ricerca findByID solo sugli utenti della stessa company
  def showUsersBySurname(userSurname: String) = SecuredAction(WithServices("admin", true)).async{ implicit request =>
    val users = userDao.findBySurname(userSurname)
    users.flatMap{
     users =>
      Future.successful(Ok(Json.toJson(users)))
    }
  }

  //lato fronted bisogna fare la ricerca findByID solo sugli utenti della stessa company
 def showUserDetails(userID: UUID) = SecuredAction(WithServices("admin", true)).async{ implicit request =>
      val user = userDao.findByID(userID)
        user.flatMap{
         user =>
        Future.successful(Ok(Json.toJson(user)))
        }
 }

  //lato fronted bisogna fare la ricerca findByID solo sugli utenti della stessa company
 def delete(userID: UUID) = SecuredAction(WithServices("admin", true)).async{ implicit request =>
  userDao.findByID(userID).flatMap{
    case None => Future.successful(BadRequest(Json.obj("message" -> "User non trovato")))
    case Some (user) =>
      val loginInfo = LoginInfo(CredentialsProvider.ID, user.email)
          if(user.company == request.identity.company)
          {
           userDao.remove(userID)
           passwordInfoDao.remove(loginInfo)
         }
          Future.successful(Ok(Json.toJson(user)))
  }
}

     //lato fronted bisogna fare aggiungere la company dell'admin (nascosta nella form)
def updateUser(userID: UUID) = SecuredAction(WithServices("admin", true)).async(parse.json) { implicit request =>
  request.body.validate[EditUser.Data].map { data =>
    userDao.findByID(userID).flatMap {
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
       val companyInfo = companyDao.findByIDUser(userID)
       val user2 = User(
          userID = user.userID,
          name = data.name,
          surname = data.surname,
          loginInfo = loginInfoNew,
          email = data.email,
          company = user.company,
          mailConfirmed = false,
          token = "vuoto",
          role = data.role,
          secretString = data.secretString
       )
       for {
         user <- userDao.update(userID,user2)
         authenticator <- env.authenticatorService.create(loginInfo)
         token <- env.authenticatorService.init(authenticator)
       } yield {
            Ok(Json.obj("token" -> "ok"))
         }
    }
  }.recoverTotal {
      case error =>
        Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
    }
 }

  def addUser = SecuredAction(WithServices("admin", true)).async(parse.json) { implicit request =>
    request.body.validate[SignUpAdmin.Data].map { data =>
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
              email = data.email,
              company = request.identity.company,
              mailConfirmed = false,
              token = "vuoto",
              role = data.role,
              secretString = data.password
            )
            val email = Email(
              "Password d'autenticazione",
              "LatexeBiscotti <latexebiscotti@gmail.com>",
              Seq("Miss TO <"+data.email+">"),
              bodyText = Some("Password per il tuo primo login in UMAP:"+data.password
              )
            )
            mailer.send(email)
            for {
              user <- userDao.save(user)
              authInfo <- authInfoRepository.add(loginInfo, authInfo)
              authenticator <- env.authenticatorService.create(loginInfo)
              token <- env.authenticatorService.init(authenticator)
            } yield {
              Ok(Json.obj("token" -> token))
            }
      }
  }.recoverTotal {
      case error =>
        Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
    }
  }


  def addChart = Action.async(parse.json) { implicit request =>
    request.body.validate[newGraphic.Data].map { data =>
      var dataList = new ListBuffer[String]
      for(nameData <- data.datas){
        dataList += nameData
      }
      if(data.thingOrModel == "Oggetto")
        {val chart = Chart(
          chartID = UUID.randomUUID(),
          functionName = data.functionName,
          thingID = data.objectID,
          thingTypeID = null,
          infoDataName = dataList
        )
        for{
          chart <- chartDao.save(chart)
          //user <- userService.save(user.copy(avatarURL = avatar))
          //authInfo <- authInfoRepository.add(loginInfo, authInfo)
          //authenticator <- env.authenticatorService.create(loginInfo)
          //token <- env.authenticatorService.init(authenticator)
        } yield {
            //env.eventBus.publish(SignUpEvent(user, request, request2Messages))
            //env.eventBus.publish(LoginEvent(user, request, request2Messages))
            Ok(Json.obj("ok" -> "ok"))
          }
        }
      else{
        val chart = Chart(
          chartID = UUID.randomUUID(),
          functionName = data.functionName,
          thingID = null,
          thingTypeID = data.objectID,
          infoDataName = dataList
        )
        for{
          chart <- chartDao.save(chart)
          //user <- userService.save(user.copy(avatarURL = avatar))
          //authInfo <- authInfoRepository.add(loginInfo, authInfo)
          //authenticator <- env.authenticatorService.create(loginInfo)
          //token <- env.authenticatorService.init(authenticator)
        } yield {
            //env.eventBus.publish(SignUpEvent(user, request, request2Messages))
            //env.eventBus.publish(LoginEvent(user, request, request2Messages))
            Ok(Json.obj("ok" -> "ok"))
          }
      }
    }.recoverTotal {
        case error =>
          Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
      }
    }
}
