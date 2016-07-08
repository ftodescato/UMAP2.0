package controllers

import javax.inject.Inject

import java.util.UUID

import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator

import models._
import models.User
import models.Engine
import models.daos.thing.ThingDAO
import models.daos.modelLogReg.ModelLogRegDAO
import models.daos.notification.NotificationDAO

import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg._

import play.api.i18n.{ MessagesApi, Messages }
import play.api.libs.mailer._
import play.api.libs.json.Json
import play.api.mvc.Action

import scala.language.postfixOps

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global



class ApplicationController @Inject() (
  val messagesApi: MessagesApi,
  modelLogRegDao: ModelLogRegDAO,
  thingDao: ThingDAO,
  notificationDao: NotificationDAO,
  mailer: MailerClient,
  val env: Environment[User, JWTAuthenticator])
  extends Silhouette[User, JWTAuthenticator] {


  def user = SecuredAction.async { implicit request =>
    Future.successful(Ok(Json.toJson(request.identity)))
  }

  // metodo engine.correlation
  def correlation(thing: Thing, datatype: Int): Double ={
    var correlation = 0.0
    //recupero dati necessari dal DB
        val data=thingDao.findListArray(thing)
        //sottoselezione dei dati
        val datalength=data.length
        var chosendata=Array.empty[Double]
        var iterator:Int=0
        for (iterator<-0 until datalength){
          chosendata=chosendata:+data(iterator)(datatype)
        }
        val e = new Engine
        //creazione della R
        var r:Array[Double]=e.getPointsOnR(chosendata)
        //chiamata di correlation
        correlation = e.getCorrelation(chosendata,r)
        //valore ritornato 0~100%==0->1
    correlation
  }

  // metodi appartenenti a sumstatistic
  def sumStatistic(thing: Thing, mv: String, datatype: Int): Double = {
    // recupero list[array[double]] dal db tramite ID
    var solution = 0.0
      val data=thingDao.findListArray(thing)
      // chiamo l'engine per calcolarmi le statistiche sui dati
      val e = new Engine
      val aux: Array[Double] = e.sumStatistic(data, mv)
      //seleziono il valore che mi interessa
      solution = aux(datatype)
    solution
  }

    def modelLogRegUpdate(thingID: UUID, oldModelID: UUID) = Action.async{ implicit request =>
      //recupero informazioni dal DB
      thingDao.findByID(thingID).flatMap{
        case None =>
          Future.successful(BadRequest(Json.obj("message" -> Messages("thing.notExists"))))
        case Some(thing) =>
          val label=thingDao.findListLabel(thing)
          val data=thingDao.findListArray(thing)
          //creo il modello di una thing
          val e = new Engine
          val modello:LogRegModel = e.getLogRegModel(thingID,label,data)

          for {
           modello <- modelLogRegDao.update(oldModelID, modello)
        } yield {
          Ok(Json.obj("token" -> "ok"))
        }
      }
  }

//   // produzione della label per una nuova misurazione
//   def logReg(thingID: UUID, data: Array[Double]):Double = {
//     // recupero il modello con l'ID della thing
//     var predizione = 0.0
//     modelLogRegDao.findByThingID(thingID).flatMap{
//       case None =>
//         Future.successful(BadRequest(Json.obj("message" -> Messages("modelLogReg.notExists"))))
//       case Some(modello) =>
//       val e = new Engine
//       // faccio la predizione della nuova label
//       predizione = e.getLogRegPrediction(modello,data)
//       Future.successful(Ok(Json.toJson(predizione)))
//     }
//     predizione
// }

  // creazione di un elemento futuro
  def futureV(thing: Thing, datatype:Int): Double = {
    var future=0.0
        val e = new Engine
        var sol=e.getFuture(thingDao.findListArray(thing))
        future=sol(datatype)
    future
  }

  def signOut = SecuredAction.async { implicit request =>
    env.eventBus.publish(LogoutEvent(request.identity, request, request2Messages))
    env.authenticatorService.discard(request.authenticator, Ok)
  }
}
