package controllers.superAdmin

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import forms.thing._
import models._
import models.Thing
import models.User
import models.DetectionDouble
import models.Measurements
import models.daos.company.CompanyDAO
import models.daos.thingType.ThingTypeDAO
import models.daos.thing.ThingDAO
// import models.daos.measurements.MeasurementsDAO
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


class ThingController @Inject() (
  //authInfoRepository: AuthInfoRepository,
  //avatarService: AvatarService,
  //passwordHasher: PasswordHasher,
  val messagesApi: MessagesApi,
  val env: Environment[User, JWTAuthenticator],
  thingDao: ThingDAO,
  thingTypeDao: ThingTypeDAO,
  // measurementsDao: MeasurementsDAO,
  companyDao: CompanyDAO)
extends Silhouette[User, JWTAuthenticator] {

  def showThing = SecuredAction(WithServices("superAdmin", true)).async{ implicit request =>
    val things = thingDao.findAll()
    things.flatMap{
      things =>
        Future.successful(Ok(Json.toJson(things)))
    }
  }

  def showThingDetails(thingID: UUID) = SecuredAction(WithServices("superAdmin", true)).async{ implicit request =>
    val thing = thingDao.findByID(thingID)
    thing.flatMap{
      thing =>
      Future.successful(Ok(Json.toJson(thing)))
    }
  }

  def delete(thingID: UUID) = SecuredAction(WithServices("superAdmin", true)).async{ implicit request =>
    thingDao.findByID(thingID).flatMap{
      case None => Future.successful(BadRequest(Json.obj("message" -> Messages("thing.notExists"))))
      case Some (thing) =>
        for{
          thing <- thingDao.remove(thingID)
        }yield{
          Ok(Json.obj("ok" -> "ok"))
         }
    }
  }

  def updateThing (thingID : UUID) = SecuredAction(WithServices("superAdmin", true)).async(parse.json) { implicit request =>
    request.body.validate[EditThing.Data].map { data =>
          thingDao.findByID(thingID).flatMap{
            case Some(thingTypeToAssign) =>
              // var detectionInitial = Detection(
              //     sensor = "",
              //     value = 0
              // )
              // var listDetectionInitial = List(detectionInitial)
              // var datasInitial = Measurements(
              //   dataTime = null,
              //   sensors = listDetectionInitial,
              //   healty = false
              // )
              // var listMeasurements = List(datasInitial)
              val thing2 = Thing(
                  thingID = thingTypeToAssign.thingID,
                  name = data.name,
                  serialNumber = data.serialNumber,
                  description = data.description,
                  thingTypeID = thingTypeToAssign.thingTypeID,
                  companyID = thingTypeToAssign.companyID,
                  datas = thingTypeToAssign.datas
              )
              for{
                thing <- thingDao.update(thingID,thing2)
              }yield {
                //env.eventBus.publish(SignUpEvent(user, request, request2Messages))
                //env.eventBus.publish(LoginEvent(user, request, request2Messages))
                Ok(Json.obj("ok" -> "ok"))
               }
            case None =>
              Future.successful(BadRequest(Json.obj("message" -> Messages("thingType.notExists"))))
          }
     }.recoverTotal {
        case error =>
          Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
       }
  }

  def addThing = Action.async(parse.json) { implicit request =>
    request.body.validate[AddThing.Data].map { data =>
      val companyInfo = data.company
      companyDao.findByID(companyInfo).flatMap{
        case Some(companyToAssign) =>
          val thingTypeInfo = data.thingTypeID
          thingTypeDao.findByID(thingTypeInfo).flatMap{
            case Some(thingTypeToAssign) =>
              //val authInfo = passwordHasher.hash(data.password)
              val thing = Thing(
                  thingID = UUID.randomUUID(),
                  name = data.thingName,
                  serialNumber = data.serialNumber,
                  description = data.description,
                  thingTypeID = data.thingTypeID,
                  companyID = data.company,
                  datas = new ListBuffer[Measurements]
              )
              for{
                thing <- thingDao.save(thing)
                //user <- userService.save(user.copy(avatarURL = avatar))
                //authInfo <- authInfoRepository.add(loginInfo, authInfo)
                //authenticator <- env.authenticatorService.create(loginInfo)
                //token <- env.authenticatorService.init(authenticator)
              } yield {
                  //env.eventBus.publish(SignUpEvent(user, request, request2Messages))
                  //env.eventBus.publish(LoginEvent(user, request, request2Messages))
                  Ok(Json.obj("ok" -> "ok"))
                }
                case None =>
                  Future.successful(BadRequest(Json.obj("message" -> Messages("thingType.notExists"))))
          }
        case None =>
          Future.successful(BadRequest(Json.obj("message" -> Messages("company.notExists"))))
      }
    }.recoverTotal {
          case error =>
            Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
      }
  }
  //Aggiunge misura alla thing
  def addMeasurements = Action.async(parse.json) { implicit request =>
    request.body.validate[AddMeasurement.Data].map { data =>
      val thingInfo = data.thingID//nuovo id
      thingDao.findByID(thingInfo).flatMap{//verifica esistenza id
        case Some(thingToAssign) =>
          val listDD = for((sensorName, valueName) <- (data.sensor zip data.value))//data.sensor data.value sono liste
            yield new DetectionDouble(sensorName, valueName)//end for
          val measurements = Measurements(
              measurementsID = UUID.randomUUID(),
              thingID = data.thingID,
              dataTime = data.dataTime,
              sensors = listDD,
              label = data.label
          )
          for{
            thing <- thingDao.updateMeasurements(thingInfo, measurements)
            //measurements <- measurementsDao.add(measurements)
          }yield {Ok(Json.obj("ok" -> "ok"))}
        case None =>
          Future.successful(BadRequest(Json.obj("message" -> Messages("thing.notExists"))))
      }
    }.recoverTotal {
          case error =>
            Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
      }
  }

  // def addDetectionDouble(thingID: UUID) = Action.async(parse.json) { implicit request =>
  //   request.body.validate[AddDetectionDouble.Data].map { data =>
  //     thingDao.findByID(thingID).flatMap{
  //       case Some(thingToAssign) =>
  //             val detectionDouble = DetectionDouble(
  //                 measurementsID = data.measurementsID,
  //                 sensor = data.sensor,
  //                 value = data.value
  //             )
  //             var measurements = thingDao.findMeasuremets(thingToAssign, data.measurementsID).flatMap{
  //               case Some(measurements) =>
  //
  //               Future.successful(measurements)
  //             }
  //             thingDao.updateDectentionDouble(thingID, measurements, detectionDouble)
  //             for{
  //               thing <- thingDao.updateMeasurements(thingID, measurements)
  //               //measurements <- measurementsDao.updateDectentionDouble(data.measurementsID,detectionDouble)
  //
  //               } yield {
  //                 Ok(Json.obj("ok" -> "ok"))
  //
  //               }
  //       case None =>
  //         Future.successful(BadRequest(Json.obj("message" -> Messages("thing.notExists"))))
  //     }
  //   }.recoverTotal {
  //         case error =>
  //           Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
  //     }
  // }
}
