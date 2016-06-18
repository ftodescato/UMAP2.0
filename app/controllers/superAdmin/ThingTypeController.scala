package controllers.superAdmin

import java.util.UUID
import javax.inject.Inject
import models.Data

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import forms.thingType._
import models._
import models.ThingType
import models.Thing
import models.User
import models.Info
import models.daos.company.CompanyDAO
import models.daos.thingType.ThingTypeDAO
import models.daos.thing.ThingDAO
import play.api.i18n.{ MessagesApi, Messages }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.Action
import scala.collection.mutable.ListBuffer

//import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
//import com.mohiva.play.silhouette.api.services.AvatarService
//import com.mohiva.play.silhouette.api.util.PasswordHasher
//import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import scala.concurrent.Future


class ThingTypeController @Inject() (
  //authInfoRepository: AuthInfoRepository,
  //avatarService: AvatarService,
  //passwordHasher: PasswordHasher,
  val messagesApi: MessagesApi,
  val env: Environment[User, JWTAuthenticator],
  thingDao: ThingDAO,
  thingTypeDao: ThingTypeDAO,
  companyDao: CompanyDAO)
extends Silhouette[User, JWTAuthenticator] {

  def showThingType = Action.async{ implicit request =>
   val thingType = thingTypeDao.findAll()
   thingType.flatMap{
    thingType =>
     Future.successful(Ok(Json.toJson(thingType)))
   }
  }
  def showThingTypeDetails(id: UUID) = SecuredAction.async{ implicit request =>
   val thingType = thingTypeDao.findByID(id)
   thingType.flatMap{
    thingType =>
     Future.successful(Ok(Json.toJson(thingType)))
   }
  }

  def addThingType = Action.async(parse.json) { implicit request =>
    request.body.validate[AddThingType.Data].map { data =>
      val companyInfo = data.company
      companyDao.checkExistence(companyInfo).flatMap {
        case false =>
          Future.successful(BadRequest(Json.obj("message" -> Messages("company.notExists"))))
        case true =>
        //val authInfo = passwordHasher.hash(data.password)
        val companyIDList = new ListBuffer[UUID]
        for( companyID <- data.company ){
          companyIDList += companyID
        }
        var dataDouble: Data = null
        if (data.listQty(0) > 0){
          var aux = new ListBuffer[Info]()//mettere per Info col bool
          for( names <- data.listDoubleValue ){
              aux+= new Info(name = names, visible = true);
          }
            dataDouble = Data(
            inUse = true,
            infos = aux
          )
        }
        val thingType = ThingType(
          thingTypeID = UUID.randomUUID(),
          thingTypeName = data.thingTypeName,
          companyID = companyIDList,
          doubleValue = dataDouble
          // valuesString = null,
          // valuesFloat = null,
          // valuesDouble = null
        )
        for {
          //user <- userService.save(user.copy(avatarURL = avatar))
          thingType <- thingTypeDao.save(thingType)
          // authInfo <- authInfoRepository.add(loginInfo, authInfo)
          // authenticator <- env.authenticatorService.create(loginInfo)
          // token <- env.authenticatorService.init(authenticator)
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


def delete(thingTypeID: UUID) = SecuredAction(WithServices("superAdmin", true)).async{ implicit request =>
    thingTypeDao.findByID(thingTypeID).flatMap{
      case None => Future.successful(BadRequest(Json.obj("message" -> Messages("thingType.notExists"))))
      case Some (thingType) =>
        for{
          thing <- thingDao.removeByThingTypeID(thingTypeID)
          thingType <- thingTypeDao.remove(thingTypeID)
        }yield {
            Ok(Json.obj("ok" -> "ok"))
          }
        }
 }

  def updateThingType(id: UUID) = SecuredAction(WithServices("superAdmin", true)).async(parse.json) { implicit request =>
    request.body.validate[EditThingType.Data].map { data =>
      val companyInfo = data.company
      companyDao.checkExistence(companyInfo).flatMap {
        case false =>
          Future.successful(BadRequest(Json.obj("message" -> Messages("company.notExists"))))
        case true =>
        thingTypeDao.findByID(id).flatMap{
          case None => Future.successful(BadRequest(Json.obj("message" -> Messages("thingType.notExists"))))
          case Some(thingType) =>
          //val authInfo = passwordHasher.hash(data.password)
          val companyIDList = new ListBuffer[UUID]
          for( companyID <- data.company ){
            companyIDList += companyID
          }
          val thingType2 = ThingType(
            thingTypeID = thingType.thingTypeID,
            thingTypeName = data.thingTypeName,
            companyID = companyIDList,
            doubleValue = thingType.doubleValue
            // valuesString = null,
            // valuesFloat = null,
            // valuesDouble = null
          )
          for {
            thingType <- thingTypeDao.update(id,thingType2)
            } yield {
            //env.eventBus.publish(SignUpEvent(user, request, request2Messages))
            //env.eventBus.publish(LoginEvent(user, request, request2Messages))
            Ok(Json.obj("ok" -> "ok"))
          }
          }
      }
}.recoverTotal {
  case error =>
    Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
}
}
}
