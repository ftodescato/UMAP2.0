package controllers.shared

//import java.io.File

//import org.apache.commons.mail.EmailAttachment
import play.api.libs.mailer._

import java.util.UUID
import javax.inject.Inject
//import play.api.libs.mailer._

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
//import forms.user._
//import forms.password._
import forms.notification._
import models._
import models.User
import models.services._
import models.daos.user._
import models.daos.notification._
//import models.daos.password._
//import models.daos.company._
import play.api.i18n.{ MessagesApi, Messages }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.Action

import scala.concurrent.Future

class NotificationController @Inject() (
  val messagesApi: MessagesApi,
  //mailer: MailerClient,
  userDao: UserDAO,
  //companyDao: CompanyDAO,
  notificationDao: NotificationDAO,
  //passwordInfoDao: PasswordInfoDAO,
  //passwordHasher: PasswordHasher,
  val env: Environment[User, JWTAuthenticator])
  extends Silhouette[User, JWTAuthenticator] {


    def showNotificationDetails(notificationID: UUID) = Action.async(parse.json) { implicit request =>
      val notification = notificationDao.find(notificationID)
      notification.flatMap{
        notification =>
        Future.successful(Ok(Json.toJson(notification)))
      }
    }

    def showNotificationOfThingType(thingTypeID: UUID) = Action.async(parse.json) { implicit request =>
      val notifications = notificationDao.find(thingTypeID)
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

   def delete(notificationID: UUID) = Action.async{ implicit request =>
     notificationDao.find(notificationID).flatMap{
       case None => Future.successful(BadRequest(Json.obj("message" -> Messages("notification.notExists"))))
       case Some (notification) =>
         for{
           notification <- notificationDao.remove(notificationID)
         }yield{
           //env.eventBus.publish(SignUpEvent(user, request, request2Messages))
           //env.eventBus.publish(LoginEvent(user, request, request2Messages))
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
        thingTypeID = null,
        thingID = data.objectID,
        valMin = data.minValue,
        valMax = data.maxValue
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
          thingTypeID = data.objectID,
          thingID = null,
          valMin = data.minValue,
          valMax = data.maxValue
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

}
