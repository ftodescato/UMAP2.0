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
import models.daos.thingType.ThingTypeDAO
import play.api.i18n.{ MessagesApi, Messages }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.libs.mailer._

import scala.concurrent.Future
import scala.collection.mutable.ListBuffer


class NotificationController @Inject() (
  val messagesApi: MessagesApi,
  userDao: UserDAO,
  notificationDao: NotificationDAO,
  thingDao: ThingDAO,
  thingTypeDao: ThingTypeDAO,
  mailer: MailerClient,
  val env: Environment[User, JWTAuthenticator])
  extends Silhouette[User, JWTAuthenticator] {


    def showNotificationDetails(notificationID: UUID) = SecuredAction(WithServices(Array("admin","user"), true)).async { implicit request =>
      val notification = notificationDao.findByID(notificationID)
      notification.flatMap{
        notification =>
          Future.successful(Ok(Json.toJson(notification)))
      }
    }

    def showNotificationOfThingType(thingTypeID: UUID) = SecuredAction(WithServices(Array("admin","user"), true)).async(parse.json) { implicit request =>
      val notifications = notificationDao.findNotificationOfThingType(thingTypeID)
      notifications.flatMap{
        notifications =>
        Future.successful(Ok(Json.toJson(notifications)))
      }
    }
    def showNotificationOfThing(thing: UUID) = SecuredAction(WithServices(Array("admin","user"), true)).async(parse.json) { implicit request =>
      val notifications = notificationDao.findNotificationOfThing(thing)
      notifications.flatMap{
        notifications =>
        Future.successful(Ok(Json.toJson(notifications)))
      }
    }

    def showNotifications = SecuredAction(WithServices(Array("admin","user"), true)).async{ implicit request =>
     val notifications = notificationDao.findAll()
     notifications.flatMap{
      notifications =>
       Future.successful(Ok(Json.toJson(notifications)))
     }
   }

   def update(notificationID: UUID) = SecuredAction(WithServices(Array("admin","user"), true)).async(parse.json){ implicit request =>
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

   def delete(notificationID: UUID) = SecuredAction(WithServices(Array("admin","user"), true)).async{ implicit request =>
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


  def addNotification = SecuredAction(WithServices(Array("admin","user"), true)).async(parse.json){ implicit request =>
    request.body.validate[AddNotification.Data].map { data =>
      userDao.findByID(request.identity.userID).flatMap{
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


  def notifyAfterMeasurementThing(thingID: UUID, measurementID: UUID, future: Boolean) = {
    var bodyMail = ""
    //cerco l'oggetto su cui si è aggiunta una misurazione tramite ID
      thingDao.findByID(thingID).flatMap{
        case None =>
          Future.successful(BadRequest(Json.obj("message" -> Messages("measurements.notExists"))))
        case Some(thing) =>
        //cerco notifiche legate a quell'oggetto
          notificationDao.findNotificationOfThing(thingID).flatMap{
            listNotifications =>
            //scorro la lista delle notifiche dell'oggetto
              for(notification <- listNotifications) {
                var parameterFind = false
                //prendo il parametro analizzato dalla notifica
                var parameter = notification.inputType
                //cerco le varie misurazioni dell'oggetto
                thingDao.findMeasurements(thingID).flatMap{
                  listOfMeasurements =>
                  //scorro la lista delle misurazioni trovate
                    for(measurement <- listOfMeasurements){
                      //verifico che sia l'ultima misurazione inserita quella da verificare per la notifica
                      if(measurement.measurementsID == measurementID){
                        //se la label è != da 0
                        if (measurement.label != 0){
                          //se si tratta di una misurazione futura
                          if(future){
                            //mando una mail a tutti gli admin di segnalazione
                            notificationToAdmin(measurement, true)
                          }
                          else{
                            //mando una mail a tutti gli admin di segnalazione
                            notificationToAdmin(measurement, false)
                          }
                        }
                        //se non ho ancora trovato il parametro della notifica continuo a ciclare sui parametri
                        for(detectionDouble <- measurement.sensors if parameterFind == false)
                        //controllo se è il parametro da analizzare
                          if(detectionDouble.sensor == parameter){
                            parameterFind = true
                            //verifico nel caso sia nel parametro corretto che il valore non sia maggiore di quello previsto
                            if(detectionDouble.value > notification.valMax){
                              bodyMail = bodyMail+"Il valore "+parameter+" è a: "+detectionDouble.value+" e il massimo previsto è per "+notification.valMax+"."
                              val email = Email(
                                "Valori "+parameter+"",
                                "LatexeBiscotti <latexebiscotti2@gmail.com>",
                                //Seq("Miss TO <"+notification.emailUser+">"),
                                Seq("Miss TO <filippo.todescato@gmail.com>"),
                                bodyText = Some(bodyMail)
                              )
                              mailer.send(email)
                            }
                            //verifico nel caso sia nel parametro corretto che il valore non sia minore di quello previsto
                            if(detectionDouble.value < notification.valMin){
                              bodyMail = bodyMail+ "Il valore "+parameter+" è a:"+detectionDouble.value+" e il minimo previsto è per"+notification.valMin+"."
                              val email = Email(
                                "Valori "+parameter+"",
                                "LatexeBiscotti <latexebiscotti2@gmail.com>",
                                //Seq("Miss TO <"+notification.emailUser+">"),
                                Seq("Miss TO <filippo.todescato@gmail.com>"),
                                bodyText = Some(bodyMail)
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

  def notifyAfterMeasurementThingType(thingTypeID: UUID, measurementID: UUID, future: Boolean) = {
    var bodyMail = ""
      thingTypeDao.findByID(thingTypeID).flatMap{
        case None =>
          Future.successful(BadRequest(Json.obj("message" -> Messages("measurements.notExists"))))
        case Some(thingType) =>
          notificationDao.findNotificationOfThingType(thingTypeID).flatMap{
            listNotifications =>
              for(notification <- listNotifications) {
                var parameterFind = false
                var parameter = notification.inputType
                thingDao.findByThingTypeID(thingTypeID).flatMap{
                  things =>
                    for(thing <- things){
                      thingDao.findMeasurements(thing.thingID).flatMap{
                        listOfMeasurements =>
                          for(measurement <- listOfMeasurements){
                            if(measurement.measurementsID == measurementID){
                              if (measurement.label != 0){
                                if(future){
                                  notificationToAdmin(measurement, true)
                                }
                                else{
                                  notificationToAdmin(measurement, false)
                                }
                              }
                              for(detectionDouble <- measurement.sensors if parameterFind == false)
                                if(detectionDouble.sensor == parameter){
                                  parameterFind = true
                                  if(detectionDouble.value > notification.valMax){
                                    if(future){
                                      bodyMail = bodyMail+"Il valore "+parameter+" nella prossima misurazione sarà a: "+detectionDouble.value+" e il massimo previsto è per "+notification.valMax+"."
                                    }
                                    else{
                                      bodyMail = bodyMail+ "Il valore "+parameter+" è a:"+detectionDouble.value+" e il massimo previsto è per"+notification.valMin+"."
                                    }
                                  }
                                  if(detectionDouble.value < notification.valMin){
                                    if(future){
                                      bodyMail = bodyMail+"Il valore "+parameter+" nella prossima misurazione sarà a: "+detectionDouble.value+" e il minimo previsto è per "+notification.valMax+"."
                                    }
                                    else{
                                      bodyMail = bodyMail+ "Il valore "+parameter+" è a:"+detectionDouble.value+" e il minimo previsto è per"+notification.valMin+"."
                                    }                                  }
                                }
                                val email = Email(
                                  "Valori "+parameter+"",
                                  "LatexeBiscotti <latexebiscotti2@gmail.com>",
                                  Seq("Miss TO <"+notification.emailUser+">"),
                                  bodyText = Some(bodyMail)
                                )
                                mailer.send(email)
                            }
                          }
                          Future.successful(Ok(Json.toJson(listOfMeasurements)))
                      }
                    }
                    Future.successful(Ok(Json.toJson(things)))
                }
            }
            Future.successful(Ok(Json.toJson(listNotifications)))
          }
      }
  }

  def notificationToAdmin(measurement: Measurements, future: Boolean) = {
    thingDao.findByID(measurement.thingID).flatMap{
      thing =>
      userDao.findByIDCompany(thing.get.companyID).flatMap{
        listUserOfCmpany =>
        var listAdmin = new ListBuffer[User]
          for(user <- listUserOfCmpany){
            if(user.role == "admin"){
              listAdmin += user
            }
          }
              for(admin <- listAdmin){
                if(future){
                  val email = Email(
                    "Stato oggetto",
                    "LatexeBiscotti <latexebiscotti2@gmail.com>",
                    Seq("Miss TO <"+admin.email+">"),
                    bodyText = Some(thing.get.name+" si troverà in stato: "+measurement.label+" nella sua prossima misurazione.")
                  )
                  mailer.send(email)
                }
                else{
                  val email = Email(
                    "Stato oggetto",
                    "LatexeBiscotti <latexebiscotti2@gmail.com>",
                    Seq("Miss TO <"+admin.email+">"),
                    bodyText = Some(thing.get.name+" è in stato: "+measurement.label+".")
                  )
                  mailer.send(email)
                }
              }
          Future.successful(listAdmin.toList)
      }

        Future.successful(Ok(Json.toJson(thing)))
    }
  }

}
