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

import scala.concurrent.Future


class FunctionController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, JWTAuthenticator],
  functionDao: FunctionDAO
)
  extends Silhouette[User, JWTAuthenticator] {


  def showFunctions = SecuredAction(WithServices(Array("superAdmin","admin"), true)).async{ implicit request =>
    val functions = functionDao.findAll()
    functions.flatMap{
      functions =>
        Future.successful(Ok(Json.toJson(functions)))
    }
  }

  def delete(name: String) = SecuredAction(WithServices(Array("superAdmin"), true)).async{ implicit request =>
    functionDao.find(name).flatMap{
      case None => Future.successful(BadRequest(Json.obj("message" -> Messages("function.notExists"))))
      case Some (function) =>
        for{
          function <- functionDao.remove(name)
        }yield{
          Ok(Json.obj("ok" -> "ok"))
         }
    }
  }

  def updateFunction (name : String) = SecuredAction(WithServices(Array("superAdmin"), true)).async(parse.json) { implicit request =>
    request.body.validate[EditFunction.Data].map { data =>
      functionDao.find(name).flatMap{
        case None => Future.successful(BadRequest(Json.obj("message" -> Messages("function.notExists"))))
        case Some (function) =>
          val newFunction = Function(
              functionID = UUID.randomUUID(),
              name = data.functionName
          )
          for{
            function <- functionDao.update(name, newFunction)
          }yield {
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
      val function = Function(
        functionID = UUID.randomUUID(),
        name = data.functionName

      )
      for{
        function <- functionDao.save(function)
      } yield {
          Ok(Json.obj("ok" -> "ok"))
        }
      }.recoverTotal {
          case error =>
            Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
        }
  }
}
