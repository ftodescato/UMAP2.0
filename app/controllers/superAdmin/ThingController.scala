  package controllers.superAdmin

import java.util.UUID
import java.util.Calendar

import javax.inject.Inject
import models.Engine

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
import models.daos.chart.ChartDAO
import models.daos.modelLogReg.ModelLogRegDAO
import models.daos.notification.NotificationDAO
import controllers.ApplicationController
import controllers.shared.adminUser.NotificationController

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
  companyDao: CompanyDAO,
  chartDao: ChartDAO,
  notificationDao: NotificationDAO,
  modelLogRegDao: ModelLogRegDAO,
  val appController: ApplicationController,
  notificationController: NotificationController
  )
extends Silhouette[User, JWTAuthenticator] {

  def showThing = SecuredAction(WithServices(Array("superAdmin"), true)).async{ implicit request =>
    val things = thingDao.findAll()
    things.flatMap{
      things =>
        Future.successful(Ok(Json.toJson(things)))
    }
  }

  def showThingDetails(thingID: UUID) = SecuredAction(WithServices(Array("superAdmin"), true)).async{ implicit request =>
    val thing = thingDao.findByID(thingID)
    thing.flatMap{
      thing =>
      Future.successful(Ok(Json.toJson(thing)))
    }
  }

  def delete(thingID: UUID) = SecuredAction(WithServices(Array("superAdmin"), true)).async{ implicit request =>
    thingDao.findByID(thingID).flatMap{
      case None => Future.successful(BadRequest(Json.obj("message" -> Messages("thing.notExists"))))
      case Some (thing) =>
        for{
          notification <- notificationDao.removeByThing(thingID)
          chart <- chartDao.removeByThing(thingID)
          thing <- thingDao.remove(thingID)
        }yield{
          Ok(Json.obj("ok" -> "ok"))
         }
    }
  }

  def updateThing (thingID : UUID) = SecuredAction(WithServices(Array("superAdmin"), true)).async(parse.json) { implicit request =>
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

  def addThing = SecuredAction(WithServices(Array("superAdmin"), true)).async(parse.json) { implicit request =>
    request.body.validate[AddThing.Data].map { data =>
      val companyInfo = data.company
      companyDao.findByID(companyInfo).flatMap{
        case Some(companyToAssign) =>
          val thingTypeInfo = data.thingTypeID
          thingTypeDao.findByID(thingTypeInfo).flatMap{
            case Some(thingTypeToAssign) =>
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
              } yield {
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
      val numberOfMeasuremnts = thingDao.countMeasurements(thingInfo)
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
                        thing <- thingDao.addMeasurements(thingInfo, measurements)
                      } yield {
                        if(numberOfMeasuremnts > 5){
                          modelLogRegDao.findByThingID(thingInfo).flatMap{
                            model =>
                            appController.modelLogRegUpdate(thingInfo, model.get.logRegModelID)
                            Future.successful(Ok(Json.toJson(model)))

                          }
                        }else{
                          if(numberOfMeasuremnts == 5){
                            appController.modelLogRegSave(thingInfo)
                          }
                        }
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

                        thing <- thingDao.addMeasurements(thingInfo, measurements)
                        } yield {
                          if(numberOfMeasuremnts > 5){
                            modelLogRegDao.findByThingID(thingInfo).flatMap{
                              model =>
                              appController.modelLogRegUpdate(thingInfo, model.get.logRegModelID)
                              Future.successful(Ok(Json.toJson(model)))

                            }
                          }else{
                            if(numberOfMeasuremnts == 5){
                              appController.modelLogRegSave(thingInfo)
                            }
                          }
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


  def addNewMeasurements = Action.async(parse.json) { implicit request =>
    request.body.validate[AddNewMeasurement.Data].map {
      data =>
      val thingInfo = data.thingID
      thingDao.findByID(thingInfo).flatMap{
        case Some(thingToAssign) =>
          thingTypeDao.findByID(thingToAssign.thingTypeID).flatMap{
            case Some(thingType) =>
              var dataThingType = thingType.doubleValue
              var listParametersthingType = new ListBuffer[String]
              for(infoThingType <- dataThingType.infos){
                listParametersthingType += infoThingType.name
              }
              if (!(listParametersthingType.equals(data.sensor))){
                  val listDD = new ListBuffer[DetectionDouble]
                  var count: Int = 0
                  var dataSensor = data.sensor
                  for (nameParameterMeasurement <- listParametersthingType){
                    if(dataSensor.contains(nameParameterMeasurement)){
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
                  var arrayDouble = Array.empty[Double]
                  for(it <- listDD){
                    arrayDouble:+it.value
                  }

                  modelLogRegDao.findByThingID(data.thingID).flatMap{
                    case None =>
                      Future.successful(BadRequest(Json.obj("message" -> Messages("modelLogReg.notExists"))))
                    case Some(modello) =>
                    val e = new Engine
                    // faccio la predizione della nuova label
                    var newLabel = e.getLogRegPrediction(modello,arrayDouble)

                  val measurements = Measurements(
                      measurementsID = UUID.randomUUID(),
                      thingID = data.thingID,
                      dataTime = data.dataTime,
                      sensors = listBufferDD,
                      label = newLabel
                      )
                      measurementsFuture(measurements)
                  for{
                    thing <- thingDao.addMeasurements(thingInfo, measurements)

                  } yield {
                    notificationController.notifyAfterMeasurementThing(thing.thingID, measurements.measurementsID, false)
                    notificationController.notifyAfterMeasurementThingType(thing.thingTypeID, measurements.measurementsID, false)
                    Ok(Json.obj("ok" -> "ok")) }
                    Future.successful(Ok(Json.toJson(newLabel)))
                  }
                }
              else{
                val listDD = for((sensorName, valueDouble) <- (data.sensor zip data.value))
                yield new DetectionDouble(sensorName, valueDouble)

                var arrayDouble = Array.empty[Double]
                for(it <- listDD){
                  arrayDouble :+ it.value
                }
                modelLogRegDao.findByThingID(data.thingID).flatMap{
                  case None =>
                    Future.successful(BadRequest(Json.obj("message" -> Messages("modelLogReg.notExists"))))
                  case Some(modello) =>
                  val e = new Engine
                  // faccio la predizione della nuova label
                  var newLabel = e.getLogRegPrediction(modello,arrayDouble)
                val measurements = Measurements(
                  measurementsID = UUID.randomUUID(),
                  thingID = data.thingID,
                  dataTime = data.dataTime,
                  sensors = listDD,
                  label = newLabel
                )
                measurementsFuture(measurements)

                for{
                  thing <- thingDao.addMeasurements(thingInfo, measurements)
                  } yield {
                    notificationController.notifyAfterMeasurementThing(thing.thingID, measurements.measurementsID, false)
                    notificationController.notifyAfterMeasurementThingType(thing.thingTypeID, measurements.measurementsID, false)
                    Ok(Json.obj("ok" -> "ok")) }
                    Future.successful(Ok(Json.toJson(newLabel)))
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

  def measurementsFuture(measurement: Measurements) = {
    thingDao.findByID(measurement.thingID).flatMap{
      thing =>
        var count = 0
        var futureSensorList = new ListBuffer[DetectionDouble]
        var arrayDouble = Array.empty[Double]
        for(sensorItem <- measurement.sensors){
          var valueFuture = appController.futureV(thing.get, count)
          val futureDD = DetectionDouble(
            sensor = sensorItem.sensor,
            value = valueFuture
          )
          futureSensorList += futureDD
          arrayDouble :+ futureDD.value
        }
        var nextDate= Calendar.getInstance()
        nextDate.setTime(measurement.dataTime)
        nextDate.add(Calendar.DAY_OF_MONTH, 1)

        modelLogRegDao.findByThingID(thing.get.thingID).flatMap{
          case None =>
            Future.successful(BadRequest(Json.obj("message" -> Messages("modelLogReg.notExists"))))
          case Some(modello) =>
          val e = new Engine
          // faccio la predizione della nuova label
          var newLabel = e.getLogRegPrediction(modello,arrayDouble)


        val futureMeasurement = Measurements(
          measurementsID = UUID.randomUUID(),
          thingID = measurement.thingID,
          dataTime = nextDate.getTime(),
          sensors = futureSensorList.toList,
          label = newLabel
        )
        thingDao.addMeasurements(measurement.thingID, futureMeasurement)
        notificationController.notifyAfterMeasurementThing(measurement.thingID, futureMeasurement.measurementsID, true)
        notificationController.notifyAfterMeasurementThingType(thing.get.thingTypeID, futureMeasurement.measurementsID, true)
        Future.successful(Ok(Json.toJson(newLabel)))
      }
     }
  }

}
