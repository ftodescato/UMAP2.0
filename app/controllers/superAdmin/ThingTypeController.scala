package controllers.superAdmin

import java.util.UUID
import javax.inject.Inject
import models.Data

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import forms.thingType._
import models.ThingType
import models.User
import models.Info
import models.daos.company.CompanyDAO
import models.daos.thingType.ThingTypeDAO
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
  //thingDao: ThingDAO,
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

  // def showCompanyDetails(companyID: UUID) = Action.async{ implicit request =>
  //   val company = companyDao.findByID(companyID)
  //   company.flatMap{
  //     company =>
  //     Future.successful(Ok(Json.toJson(company)))
  //   }
  // }

  // def delete(thingID: UUID) = Action.async{ implicit request =>
  //   thingDao.findByID(thingID).flatMap{
  //     case None => Future.successful(BadRequest(Json.obj("message" -> Messages("thing.notExists"))))
  //     case Some (thing) =>
  //       for{
  //         thing <- thingDao.remove(thingID)
  //       }yield{
  //         //env.eventBus.publish(SignUpEvent(user, request, request2Messages))
  //         //env.eventBus.publish(LoginEvent(user, request, request2Messages))
  //         Ok(Json.obj("ok" -> "ok"))
  //        }
  //   }
  // }

  // def updateThing (thingID : UUID) = Action.async(parse.json) { implicit request =>
  //   request.body.validate[EditThing.Data].map { data =>
  //     val companyInfo = data.company
  //     companyDao.findByID(companyInfo).flatMap{
  //       case Some (companyToAssign) =>
  //         val thingTypeInfo = data.thingTypeID
  //         thingTypeDao.findByID(thingTypeInfo).flatMap{
  //           case Some(thingTypeToAssign) =>
  //             val thing2 = Thing(
  //                 thingID = UUID.randomUUID(),
  //                 name = data.thingName,
  //                 serialNumber = data.serialNumber,
  //                 description = data.description,
  //                 thingTypeID = data.thingTypeID,
  //                 companyID = data.company
  //             )
  //             for{
  //               thing <- thingDao.update(thingID,thing2)
  //             }yield {
  //               //env.eventBus.publish(SignUpEvent(user, request, request2Messages))
  //               //env.eventBus.publish(LoginEvent(user, request, request2Messages))
  //               Ok(Json.obj("ok" -> "ok"))
  //              }
  //           case None =>
  //             Future.successful(BadRequest(Json.obj("message" -> Messages("thingType.notExists"))))
  //         }
  //       case None =>
  //        Future.successful(BadRequest(Json.obj("message" -> Messages("company.notExists"))))
  //      }
  //    }.recoverTotal {
  //       case error =>
  //         Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
  //      }
  // }

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
            valuee = aux
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
}
