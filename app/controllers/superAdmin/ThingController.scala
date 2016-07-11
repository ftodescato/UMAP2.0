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
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future


class ThingController @Inject() (
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

//metodo per inserire misurazioni iniziali che andranno a costruire un modello per i label delle misurazioni e in seguito aggiornarlo
  def addMeasurements = Action.async(parse.json) { implicit request =>
    //richiamo la form forms.thing.addNewMeasurement
    request.body.validate[AddMeasurement.Data].map {
      data =>
      //ottengo l'ID dell'oggetto dato nella form
      val thingInfo = data.thingID
      //cerco l'oggetto con l'ID della form
      thingDao.findByID(thingInfo).flatMap{
        //se lo trovo
        case Some(thingToAssign) =>
        //cerco il modello dell'oggetto
        thingTypeDao.findByID(thingToAssign.thingTypeID).flatMap{
          //se lo trovo
          case Some(thingType) =>
            var numberOfMeasurements = 0      //variabile per contare il numero di misurazioni di un oggetto
            //ciclo le misurazioni dell'oggetto con ID inserito nella form
            for(measurement <- thingToAssign.datas){
              //aggiungo 1 alle numero di misurazioni
              numberOfMeasurements = numberOfMeasurements + 1
            }
            var dataThingType = thingType.doubleValue       //variabile contenente il dataDouble del modello dell'oggetto
            //creo una ListBuffer che conterrà la lista dei dataDouble del modello dell'oggetto
            var listParametersthingType = new ListBuffer[String]
            //ciclo la lista di Info del modello dell'oggetto
            for(infoThingType <- dataThingType.infos)
              {//popolo con i vari Info name listParametersthingType
                listParametersthingType += infoThingType.name
              }
              //controllo che listParametersthingType sia diverso da i parametri passati nella form
              if (!(listParametersthingType.equals(data.sensor)))
                {//creo una nuova ListBuffer che conterrà i DetectionDouble
                  val listDD = new ListBuffer[DetectionDouble]
                  var count: Int = 0    //contatore
                  var dataSensor = data.sensor      //lista di parametri passati dalla form
                  //ciclo la listParametersthingType(diversa da i parameti passati dalla form)
                  for (nameParameterMeasurement <- listParametersthingType)
                  {//per ogni Info controllo che sia contenuto tra la lista dei parametri passati dalla form
                    if(dataSensor.contains(nameParameterMeasurement))
                      {// creo un nuovo detectioDouble(con il nome del parametro contenuto dai parametri passati dalla form)
                        var dD = new DetectionDouble(nameParameterMeasurement, data.value(count))
                        //aggiungo il dD alla listaDD
                        listDD += dD
                        count = count + 1
                      }
                      else{//se il parametro non è contenuto nei parametri passati nella form creo un DD con valore fuori range(1000000.0)
                        var dD = new DetectionDouble(nameParameterMeasurement, 1000000.0)
                        listDD += dD
                      }
                  }
                  val listBufferDD = listDD.toList
                  //creazione misurazione con la lista aggiornata dei DD
                  val measurements = Measurements(
                    measurementsID = UUID.randomUUID(),
                    thingID = data.thingID,
                    dataTime = data.dataTime,
                    sensors = listBufferDD,
                    label = data.label
                  )
                  for{//aggiungo alla collection la nuova misurazione
                    thing <- thingDao.addMeasurements(thingInfo, measurements)
                  } yield {//verifico che il numero di misurazioni sia superiore a 5
                      if(numberOfMeasurements > 5){//se lo è cerco il modello per la label in base all'ID dell'oggetto
                        modelLogRegDao.findByThingID(thingInfo).flatMap{
                          model =>
                          //aggiorno il modello aggiungendo la nuova misurazione
                            appController.modelLogRegUpdate(thingInfo, model.get.logRegModelID)
                            Future.successful(Ok(Json.toJson(model)))
                        }
                      }
                      else{// se le misurazioni sono uguali a 5
                        if(numberOfMeasurements == 5){//cerco
                          thingDao.findByID(thingToAssign.thingID).flatMap{
                            case None =>
                              Future.successful(BadRequest(Json.obj("message" -> Messages("thing.notExists"))))
                            case Some(thing) =>
                              val label=thingDao.findListLabel(thing)
                              val data=thingDao.findListArray(thing)
                              //creo il modello di una thing
                              val e = new Engine
                              val modello:LogRegModel = e.getLogRegModel(thingToAssign.thingID,label,data)
                              for {
                                 modello <- modelLogRegDao.save(modello)
                              } yield {
                                Ok(Json.obj("ok" -> "ok"))
                                }
                          }
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
                      println(numberOfMeasurements)
                      for{
                        thing <- thingDao.addMeasurements(thingInfo, measurements)
                        } yield {
                          if(numberOfMeasurements > 5){
                            modelLogRegDao.findByThingID(thingInfo).flatMap{
                              model =>
                              appController.modelLogRegUpdate(thingInfo, model.get.logRegModelID)
                              Future.successful(Ok(Json.toJson(model)))

                            }
                          }else{
                            if(numberOfMeasurements == 5){
                              thingDao.findByID(thingToAssign.thingID).flatMap{
                                case None =>
                                  Future.successful(BadRequest(Json.obj("message" -> Messages("thing.notExists"))))
                                case Some(thing) =>
                                  val label=thingDao.findListLabel(thing)
                                  val data=thingDao.findListArray(thing)
                                  //creo il modello di una thing
                                  val e = new Engine
                                  val modello:LogRegModel = e.getLogRegModel(thingToAssign.thingID,label,data)

                                  for {
                                   modello <- modelLogRegDao.save(modello)
                                } yield {
                                  Ok(Json.obj("ok" -> "ok"))
                                }
                              }
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

//metodo per aggiungere misurazioni senza label che verrà calcolata in base al modello
  def addNewMeasurements = Action.async(parse.json) { implicit request =>
    request.body.validate[AddNewMeasurement.Data].map {
      data =>
      val thingInfo = data.thingID
      //cerco l'oggetto su cui inserire la nuova misurazione
      thingDao.findByID(thingInfo).flatMap{
        //se lo trovo
        case Some(thingToAssign) =>
        //cerco il thingType di questto oggetto
          thingTypeDao.findByID(thingToAssign.thingTypeID).flatMap{
            //se lo trovo
            case Some(thingType) =>
              var dataThingType = thingType.doubleValue //mi prendo la lista di parametri
              var listParametersthingType = new ListBuffer[String]  //creo una nuova ListBuffer di parametri vuota
              //ciclo i vari parametri del modello dell'oggetto con la nuova misurazione da inserire
              for(infoThingType <- dataThingType.infos){
                //e inserisco i parametri all'interno della nuova lista
                listParametersthingType += infoThingType.name
              }
              //controllo che la nuova lista sia diversa alla lista di parametri inserita nella nuova misurazione
              if (!(listParametersthingType.equals(data.sensor))){

                  modelLogRegDao.findByThingID(data.thingID).flatMap{
                    case None =>
                      Future.successful(BadRequest(Json.obj("message" -> Messages("modelLogReg.notExists"))))
                    case Some(modello) =>
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
                    var arrayDouble =new ArrayBuffer[Double]
                    for(it <- listDD){
                      arrayDouble+=it.value
                    }
                    val e = new Engine
                    // faccio la predizione della nuova label
                    var newLabel = e.getLogRegPrediction(modello,arrayDouble.toArray)
                  //creo nuova misurazione
                  val measurements = Measurements(
                      measurementsID = UUID.randomUUID(),
                      thingID = data.thingID,
                      dataTime = data.dataTime,
                      sensors = listBufferDD,
                      label = newLabel
                      )
                  for{//aggiungo la misurazione alla collection
                    thing <- thingDao.addMeasurements(thingInfo, measurements)
                  } yield {//richiamo le eventuali notifiche
                    notificationController.notifyAfterMeasurementThing(thing.thingID, measurements.measurementsID, false)
                    notificationController.notifyAfterMeasurementThingType(thing.thingTypeID, measurements.measurementsID, false)
                    Ok(Json.obj("ok" -> "ok")) }
                  }
                }
                //se la lista è uguale
              else{
                //cerco il modello label dell'oggetto a cui voglio inserire una misurazione
                modelLogRegDao.findByThingID(data.thingID).flatMap{
                  case None =>
                    Future.successful(BadRequest(Json.obj("message" -> Messages("modelLogReg.notExists"))))
                  //se lo trovo
                  case Some(modello) =>
                  //ciclo doppio in cui inserisco i valori della misurazione da inserire
                  val listDD = for((sensorName, valueDouble) <- (data.sensor zip data.value))
                  yield new DetectionDouble(sensorName, valueDouble)
                  //arrayBuffer vuoto contenente i DD value della misurazione
                  var arrayDouble =new ArrayBuffer[Double]
                  for(it <- listDD){
                    arrayDouble+=it.value
                  }
                  val e = new Engine
                  // faccio la predizione della nuova label
                  var newLabel = e.getLogRegPrediction(modello,arrayDouble.toArray)
                  //creo la nuova misurazione con label
                  val measurements = Measurements(
                  measurementsID = UUID.randomUUID(),
                  thingID = data.thingID,
                  dataTime = data.dataTime,
                  sensors = listDD,
                  label = newLabel
                )

                var count = 0
                //new ListBuffer contenente
                var futureSensorList = new ListBuffer[DetectionDouble]
                var arrayDoubleF = new ArrayBuffer[Double]
                //ciclo sui sui DD della misurazione appena inserita
                for(sensorItem <- measurements.sensors){
                  //calcolo il futuro per il valore DD in posizione count
                  var valueFuture = appController.futureV(thingToAssign, count)
                  //creo un nuovo DD con il valore futuro
                  val futureDD = DetectionDouble(
                    sensor = sensorItem.sensor,
                    value = valueFuture
                  )
                  //aggiungo alla listaBuffer il nuovo DD
                  futureSensorList += futureDD
                  arrayDoubleF += futureDD.value
                }
                //aggiungo la data della misurazione futura impostandola ad il giorno sucessivo all'ultima ricevuta
                var nextDate= Calendar.getInstance()
                nextDate.setTime(measurements.dataTime)
                nextDate.add(Calendar.DAY_OF_MONTH, 1)
                var newLabelF = e.getLogRegPrediction(modello,arrayDoubleF.toArray)

                //creo la misurazione futura
                val futureMeasurement = Measurements(
                  measurementsID = UUID.randomUUID(),
                  thingID = data.thingID,
                  dataTime = nextDate.getTime(),
                  sensors = futureSensorList.toList,
                  label = newLabelF
                )
                //aggiungo momentaneamente il futureMeasurement tra le misurazioni
                thingDao.addMeasurements(thingInfo, futureMeasurement)
                //richiamo le notifiche
                notificationController.notifyAfterMeasurementThing(thingToAssign.thingID, futureMeasurement.measurementsID, true)
                notificationController.notifyAfterMeasurementThingType(thingToAssign.thingTypeID, futureMeasurement.measurementsID, true)
                for{
                  //aggiungo la nuova misurazione reale
                  thing <- thingDao.addMeasurements(thingInfo, measurements)
                  } yield {
                    //richiamo le ntifiche se necessario sulla misurazione
                    notificationController.notifyAfterMeasurementThing(thingToAssign.thingID, measurements.measurementsID, false)
                    notificationController.notifyAfterMeasurementThingType(thingToAssign.thingTypeID, measurements.measurementsID, false)
                    Ok(Json.obj("ok" -> "ok")) }
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

}
