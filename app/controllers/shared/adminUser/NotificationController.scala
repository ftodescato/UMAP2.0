package controllers.shared.adminUser

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import forms.notification._
import models._
import models.User
import models.services._
import models.daos.user._
import models.daos.notification._
import models.daos.thing.ThingDAO
import play.api.i18n.{ MessagesApi, Messages }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.libs.mailer._

import scala.concurrent.Future

class NotificationController @Inject() (
  val messagesApi: MessagesApi,
  userDao: UserDAO,
  notificationDao: NotificationDAO,
  thingDao: ThingDAO,
  mailer: MailerClient,
  val env: Environment[User, JWTAuthenticator])
  extends Silhouette[User, JWTAuthenticator] {


    def showNotificationDetails(notificationID: UUID) = Action.async { implicit request =>
      val notification = notificationDao.findByID(notificationID)
      notification.flatMap{
        notification =>
          Future.successful(Ok(Json.toJson(notification)))
      }
    }

    def showNotificationOfThingType(thingTypeID: UUID) = Action.async(parse.json) { implicit request =>
      val notifications = notificationDao.findNotificationOfThingType(thingTypeID)
      notifications.flatMap{
        notifications =>
        Future.successful(Ok(Json.toJson(notifications)))
      }
    }
    def showNotificationOfThing(thing: UUID) = Action.async(parse.json) { implicit request =>
      val notifications = notificationDao.findNotificationOfThing(thing)
      notifications.flatMap{
        notifications =>
        Future.successful(Ok(Json.toJson(notifications)))
      }
    }

    def showNotifications = Action.async{ implicit request =>
     val notifications = notificationDao.findAll()
     notifications.flatMap{
      notifications =>
       Future.successful(Ok(Json.toJson(notifications)))
     }
   }

   def update(notificationID: UUID) = Action.async(parse.json){ implicit request =>
     request.body.validate[EditNotification.Data].map { data =>
       notificationDao.findByID(notificationID).flatMap{
         case None => Future.successful(BadRequest(Json.obj("message" -> Messages("notification.notExists"))))
         case Some (notification) =>
         if(notification.isThing == true)
          {  val newNotification = Notification(
             notificationID = notification.notificationID,
             notificationDescription = data.notificationDescription,
             emailUser = notification.emailUser,
             inputType = notification.inputType,
             thingID = notification.thingID,
             thingTypeID = None,
             valMin = data.valMin,
             valMax= data.valMax,
             isThing = notification.isThing
           )
           for{
             notification <- notificationDao.update(notificationID, newNotification)
           }yield {
             Ok(Json.obj("ok" -> "ok"))
            }
         }
         else{
           val newNotification = Notification(
              notificationID = notification.notificationID,
              notificationDescription = data.notificationDescription,
              emailUser = notification.emailUser,
              inputType = notification.inputType,
              thingID = None,
              thingTypeID = notification.thingTypeID,
              valMin = data.valMin,
              valMax= data.valMax,
              isThing = notification.isThing
            )
           for{
             notification <- notificationDao.update(notificationID, newNotification)
           }yield {
             Ok(Json.obj("ok" -> "ok"))
            }
          }
       }
     }.recoverTotal {
         case error =>
           Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
       }
     }

   def delete(notificationID: UUID) = Action.async{ implicit request =>
     notificationDao.findByID(notificationID).flatMap{
       case None => Future.successful(BadRequest(Json.obj("message" -> Messages("notification.notExists"))))
       case Some (notification) =>
         for{
           notification <- notificationDao.remove(notificationID)
         }yield{
           Ok(Json.obj("ok" -> "ok"))
          }
     }
   }


  def addNotification(userID: UUID) = Action.async(parse.json) { implicit request =>
    request.body.validate[AddNotification.Data].map { data =>
      userDao.findByID(userID).flatMap{
        case None => Future.successful(BadRequest(Json.obj("message" -> Messages("user.notExists"))))
        case Some(user) =>
      if(data.modelOrThing == "Oggetto"){
      val notification = Notification(
        notificationID = UUID.randomUUID(),
        notificationDescription = data.description,
        emailUser = user.email,
        inputType = data.parameter,
        thingTypeID = None,
        thingID = Some(data.objectID),
        valMin = data.minValue,
        valMax = data.maxValue,
        isThing = true
      )
      for{
        notification <- notificationDao.save(notification)
      } yield {
          Ok(Json.obj("ok" -> "ok"))
        }
      }
      else{
        val notification = Notification(
          notificationID = UUID.randomUUID(),
          notificationDescription = data.description,
          emailUser =user.email,
          inputType = data.parameter,
          thingTypeID = Some(data.objectID),
          thingID = None,
          valMin = data.minValue,
          valMax = data.maxValue,
          isThing = false
        )
        for{
          notification <- notificationDao.save(notification)
        } yield {
            Ok(Json.obj("ok" -> "ok"))
          }
      }
    }
    }.recoverTotal {
      case error =>
        Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
      }
  }


  def notifyAfterMeasurement(thingID: UUID, measurementID: UUID) ={
      thingDao.findByID(thingID).flatMap{
        case None =>
          Future.successful(BadRequest(Json.obj("message" -> Messages("measurements.notExists"))))
        case Some(thing) =>
          notificationDao.findNotificationOfThing(thingID).flatMap{
            listNotifications =>
              for(notification <- listNotifications) {
                var parameterFind = false
                var parameter = notification.inputType
                thingDao.findMeasurements(thingID).flatMap{
                  listOfMeasurements =>
                    for(measurement <- listOfMeasurements){
                      if(measurement.measurementsID == measurementID){
                        for(detectionDouble <- measurement.sensors if parameterFind == false)
                          if(detectionDouble.sensor == parameter){
                            parameterFind = true
                            if(detectionDouble.value > notification.valMax){
                              val email = Email(
                                "Valori "+parameter+"",
                                "LatexeBiscotti <latexebiscotti@gmail.com>",
                                Seq("Miss TO <"+notification.emailUser+">"),
                                bodyText = Some("Il valore "+parameter+"è a:"+detectionDouble.value+" e il massimo previsto è per"+notification.valMax
                                )
                              )
                              mailer.send(email)
                            }
                            if(detectionDouble.value < notification.valMin){
                              val email = Email(
                                "Valori "+parameter+"",
                                "LatexeBiscotti <latexebiscotti@gmail.com>",
                                Seq("Miss TO <"+notification.emailUser+">"),
                                bodyText = Some("Il valore "+parameter+"è a:"+detectionDouble.value+" e il minimo previsto è per"+notification.valMin
                                )
                              )
                              mailer.send(email)
                            }
                          }
                      }
                    }
                    Future.successful(Ok(Json.toJson(listOfMeasurements)))
                }

            }
            Future.successful(Ok(Json.toJson(listNotifications)))
          }
      }
  }

}
