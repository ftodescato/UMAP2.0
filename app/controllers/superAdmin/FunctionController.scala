package controllers.superAdmin

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import forms.function._
import models._
import models.Function
import models.daos.function.FunctionDAO
import play.api.i18n.{ MessagesApi, Messages }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.Action

//import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
//import com.mohiva.play.silhouette.api.services.AvatarService
//import com.mohiva.play.silhouette.api.util.PasswordHasher
//import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import scala.concurrent.Future
//import scala.collection.mutable.ListBuffer


class FunctionController @Inject() (
  //authInfoRepository: AuthInfoRepository,
  //avatarService: AvatarService,
  //passwordHasher: PasswordHasher,
  val messagesApi: MessagesApi,
  val env: Environment[User, JWTAuthenticator],
  //companyDao: CompanyDAO,
  functionDao: FunctionDAO
  //userDao: UserDAO
)
  extends Silhouette[User, JWTAuthenticator] {


  def showFunctions = SecuredAction(WithServices("superAdmin", true)).async{ implicit request =>
    val functions = functionDao.findAll()
    functions.flatMap{
      functions =>
        Future.successful(Ok(Json.toJson(functions)))
    }
  }

  def delete(name: String) = SecuredAction(WithServices("superAdmin", true)).async{ implicit request =>
    functionDao.find(name).flatMap{
      case None => Future.successful(BadRequest(Json.obj("message" -> Messages("function.notExists"))))
      case Some (function) =>
        for{
          function <- functionDao.remove(name)
        }yield{
          //env.eventBus.publish(SignUpEvent(user, request, request2Messages))
          //env.eventBus.publish(LoginEvent(user, request, request2Messages))
          Ok(Json.obj("ok" -> "ok"))
         }
    }
  }

  def updateFunction (name : String) = SecuredAction(WithServices("superAdmin", true)).async(parse.json) { implicit request =>
    request.body.validate[EditFunction.Data].map { data =>
      functionDao.find(name).flatMap{
        case None => Future.successful(BadRequest(Json.obj("message" -> Messages("function.notExists"))))
        case Some (function) =>
          val newFunction = Function(
              name = data.functionName
          )
          for{
            function <- functionDao.update(name,newFunction)
          }yield {
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

  def addFunction = UserAwareAction.async(parse.json) { implicit request =>
    request.body.validate[AddFunction.Data].map { data =>
      //val authInfo = passwordHasher.hash(data.password)
      val function = Function(
          name = data.functionName
      )
      for{
        function <- functionDao.save(function)
        //user <- userService.save(user.copy(avatarURL = avatar))
        //authInfo <- authInfoRepository.add(loginInfo, authInfo)
        //authenticator <- env.authenticatorService.create(loginInfo)
        //token <- env.authenticatorService.init(authenticator)
      } yield {
          //env.eventBus.publish(SignUpEvent(user, request, request2Messages))
          //env.eventBus.publish(LoginEvent(user, request, request2Messages))
          Ok(Json.obj("ok" -> "ok"))
        }
      }.recoverTotal {
          case error =>
            Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
        }
  }
}
