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
import models.Engine
import models.daos.company.CompanyDAO
import models.daos.thingType.ThingTypeDAO
import models.daos.thing.ThingDAO
import play.api.i18n.{ MessagesApi, Messages }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.Action

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future


//import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
//import com.mohiva.play.silhouette.api.services.AvatarService
//import com.mohiva.play.silhouette.api.util.PasswordHasher
//import com.mohiva.play.silhouette.impl.providers.CredentialsProvider


class ThingController @Inject() (
  //authInfoRepository: AuthInfoRepository,
  //avatarService: AvatarService,
  //passwordHasher: PasswordHasher,
  val messagesApi: MessagesApi,
  val env: Environment[User, JWTAuthenticator],
  thingDao: ThingDAO,
  thingTypeDao: ThingTypeDAO,
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

  def addMeasurements = Action.async(parse.json) { implicit request =>
    request.body.validate[AddMeasurement.Data].map {
      data =>
      val thingInfo = data.thingID
      thingDao.findByID(thingInfo).flatMap{
        case Some(thingToAssign) =>
        thingTypeDao.findByID(thingToAssign.thingTypeID).flatMap{
          case Some(thingType) =>
            var dataThingType = thingType.doubleValue
            var listParametersthingType = new ListBuffer[String]
            for(infoThingType <- dataThingType.infos)
              {
                listParametersthingType += infoThingType.name
              }
              if (!(listParametersthingType.equals(data.sensor)))
                {
                  val listDD = new ListBuffer[DetectionDouble]
                  var count: Int = 0
                  var dataSensor = data.sensor
                  for (nameParameterMeasurement <- listParametersthingType)
                  {
                    if(dataSensor.contains(nameParameterMeasurement))
                      {
                        var dD = new DetectionDouble(nameParameterMeasurement, data.value(count))
                        listDD += dD
                        count = count + 1
                      }
                      else{
                        var dD = new DetectionDouble(nameParameterMeasurement, 1000000.0)
                        listDD += dD
                      }
                  }
                      val listBufferDD = listDD.toList
                      val measurements = Measurements(
                          measurementsID = UUID.randomUUID(),
                          thingID = data.thingID,
                          dataTime = data.dataTime,
                          sensors = listBufferDD,
                          label = data.label
                      )
                      for{

                        thing <- thingDao.updateMeasurements(thingInfo, measurements)
                        //measurements <- measurementsDao.add(measurements)
                        } yield {
                          Ok(Json.obj("ok" -> "ok"))

                        }
                }
              else
              {
                val listDD = for((sensorName, valueDouble) <- (data.sensor zip data.value))
                yield new DetectionDouble(sensorName, valueDouble)
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
                        } yield {
                          Ok(Json.obj("ok" -> "ok"))

                        }
              }
          case None => Future.successful(BadRequest(Json.obj("message" -> Messages("thingType.notExists"))))

        }
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
