package controllers.superAdmin

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator

import forms.thingType._

import models._
import models.ThingType
import models.Thing
import models.User
import models.Info
import models.DataDouble
import models.daos.company.CompanyDAO
import models.daos.notification.NotificationDAO
import models.daos.thingType.ThingTypeDAO
import models.daos.thing.ThingDAO

import play.api.i18n.{ MessagesApi, Messages }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.Action

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future


class ThingTypeController @Inject() (
  //authInfoRepository: AuthInfoRepository,
  //avatarService: AvatarService,
  //passwordHasher: PasswordHasher,
  val messagesApi: MessagesApi,
  val env: Environment[User, JWTAuthenticator],
  thingController: ThingController,
  thingDao: ThingDAO,
  thingTypeDao: ThingTypeDAO,
  notificationDao: NotificationDAO,
  companyDao: CompanyDAO)
extends Silhouette[User, JWTAuthenticator] {

  def showThingType = SecuredAction(WithServices(Array("superAdmin"), true)).async{ implicit request =>
   val thingType = thingTypeDao.findAll()
   thingType.flatMap{
    thingType =>
     Future.successful(Ok(Json.toJson(thingType)))
   }
  }
  def showThingTypeDetails(id: UUID) = SecuredAction(WithServices(Array("superAdmin"), true)).async{ implicit request =>
   val thingType = thingTypeDao.findByID(id)
   thingType.flatMap{
    thingType =>
     Future.successful(Ok(Json.toJson(thingType)))
   }
  }

  def addThingType = SecuredAction(WithServices(Array("superAdmin"), true)).async(parse.json) { implicit request =>
    request.body.validate[AddThingType.Data].map { data =>
      val companyInfo = data.company
      companyDao.findAll().flatMap{
        listCompany =>
          if(companyInfo.equals(listCompany)){
            val companyIDList = new ListBuffer[UUID]
            for( companyID <- data.company ){
              companyIDList += companyID
            }
            var dataDouble: DataDouble = null
            if (data.listQty(0) > 0){
              var aux = new ListBuffer[Info]()//mettere per Info col bool
              for( names <- data.listDoubleValue ){
                  aux+= new Info(name = names, visible = true);
              }
                dataDouble = DataDouble(
                inUse = true,
                infos = aux
              )
            }
            val thingType = ThingType(
              thingTypeID = UUID.randomUUID(),
              thingTypeName = data.thingTypeName,
              companyID = companyIDList,
              doubleValue = dataDouble
            )
            for {
              thingType <- thingTypeDao.save(thingType)
            } yield {
              Ok(Json.obj("ok" -> "ok"))
            }
        }
        else{
          Future.successful(BadRequest(Json.obj("message" -> Messages("company.notExists"))))
        }
      }

}.recoverTotal {
  case error =>
    Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
}
}


def delete(thingTypeID: UUID) = SecuredAction(WithServices(Array("superAdmin"), true)).async{ implicit request =>
    thingTypeDao.findByID(thingTypeID).flatMap{
      case None => Future.successful(BadRequest(Json.obj("message" -> Messages("thingType.notExists"))))
      case Some (thingType) =>
      var listThingWithThisTT = new ListBuffer[Thing]
      for (thingWithThisTT <- thingDao.findByThingTypeID(thingTypeID))
      {
        for(thing <- thingWithThisTT)
        {
          listThingWithThisTT += thing
        }
      }
      for (thing <-listThingWithThisTT)
      {
          thingController.delete(thing.thingID)
      }
        for{
          notification <- notificationDao.removeByThingType(thingTypeID)
          //thing <- thingDao.removeByThingTypeID(thingTypeID)
          thingType <- thingTypeDao.remove(thingTypeID)

        }yield {
            Ok(Json.obj("ok" -> "ok"))
          }
        }
 }

  def updateThingType(id: UUID) = SecuredAction(WithServices(Array("superAdmin"), true)).async(parse.json) { implicit request =>
    request.body.validate[EditThingType.Data].map { data =>
      val companyInfo = data.company
      companyDao.findAll().flatMap{
        listCompany =>
          if(companyInfo.equals(listCompany)){
        thingTypeDao.findByID(id).flatMap{
          case None => Future.successful(BadRequest(Json.obj("message" -> Messages("thingType.notExists"))))
          case Some(thingType) =>
          val companyIDList = new ListBuffer[UUID]
          for( companyID <- data.company ){
            companyIDList += companyID
          }
          val thingType2 = ThingType(
            thingTypeID = thingType.thingTypeID,
            thingTypeName = data.thingTypeName,
            companyID = companyIDList,
            doubleValue = thingType.doubleValue
          )
          for {
            thingType <- thingTypeDao.update(id,thingType2)
            } yield {
            Ok(Json.obj("ok" -> "ok"))
          }
          }
    }
    else{
      Future.successful(BadRequest(Json.obj("message" -> Messages("company.notExists"))))
    }
  }
}.recoverTotal {
  case error =>
    Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
}
}
}
