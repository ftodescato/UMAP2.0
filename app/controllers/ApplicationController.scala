package controllers
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import java.util.UUID
import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models.User
import models.Engine
import models._
import models.daos.thing.ThingDAO
import models.daos.modelLogReg.ModelLogRegDAO
import models.daos.notification.NotificationDAO
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.mvc.Action
import scala.concurrent.Future
import org.apache.spark.mllib.linalg._
import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.mllib.regression.LabeledPoint

import play.api.i18n.{ MessagesApi, Messages }

import play.api.libs.mailer._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
/**
 * The basic application controller.
 *
 * @param messagesApi The Play messages API.
 * @param env The Silhouette environment.
 * @param socialProviderRegistry The social provider registry.
 */
class ApplicationController @Inject() (
  val messagesApi: MessagesApi,
  modelLogRegDao: ModelLogRegDAO,
  thingDao: ThingDAO,
  notificationDao: NotificationDAO,
  mailer: MailerClient,
  val env: Environment[User, JWTAuthenticator])
  extends Silhouette[User, JWTAuthenticator] {

  /**
   * Returns the user.
   *
   * @return The result to display.
   */
  def user = SecuredAction.async { implicit request =>
    Future.successful(Ok(Json.toJson(request.identity)))
  }

  def test = UserAwareAction.async { implicit request =>
  Future.successful(Ok(Json.obj("test"->"test")))
  }
// metodo engine.correlation
  def correlation(thingID: UUID, datatype: Int): Double ={
    //recupero dati necessari dal DB
    val thingDB =thingDao.findByID(thingID)
    val thing = Await.result(thingDB, 1 seconds)
    val data=thingDao.findListArray(thing.get)
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
    val sol: Double = e.getCorrelation(chosendata,r)
    //valore ritornato 0~100%==0->1
    sol
  }
  // metodi appartenenti a sumstatistic
  def sumStatistic(thingID: UUID, mv: String, datatype: Int): Double = {
    // recupero list[array[double]] dal db tramite ID
    val thingDB =thingDao.findByID(thingID)
    val thing = Await.result(thingDB, 1 seconds)
    val data=thingDao.findListArray(thing.get)
    // chiamo l'engine per calcolarmi le statistiche sui dati
    val e = new Engine
    val aux: Array[Double] = e.sumStatistic(data, mv)

    //seleziono il valore che mi interessa
    var sol:Double=aux(datatype)
    //valore ritornato -inf->+inf
    sol
  }
  // creaziome del modello degli oggetti a partire dai dati nel DB
  def modelLogReg(thingID: UUID) = Action.async{ implicit request =>
    //recupero informazioni dal DB
    thingDao.findByID(thingID).flatMap{
      case None =>
        Future.successful(BadRequest(Json.obj("message" -> Messages("thing.notExists"))))
      case Some(thing) =>
      //  val thing = Await.result(thingDB, 1 seconds)
        val label=thingDao.findListLabel(thing)
        val data=thingDao.findListArray(thing)

        //creo il modello di una thing
        val e = new Engine
        val modello:LogRegModel = e.getLogRegModel(thingID,label,data)

        for {
         modello <- modelLogRegDao.save(modello)
      } yield {
        Ok(Json.obj("token" -> "ok"))
      }
    }

  }
  // produzione della label per una nuova misurazione
  def LogReg(thingID: UUID, data: Array[Double]):Double = {
    // recupero il modello con l'ID della thing
    var predizione = 0.0
    modelLogRegDao.findByThingID(thingID).flatMap{
      case None =>
        Future.successful(BadRequest(Json.obj("message" -> Messages("modelLogReg.notExists"))))
      case Some(modello) =>

      val e = new Engine
      // faccio la predizione della nuova label
      predizione = e.getLogRegPrediction(modello,data)
      Future.successful(Ok(Json.toJson(predizione)))
    }
    predizione
}

  // creazione di un elemento futuro
  def futureV(thingID: UUID, datatype:Int): Double = {
    //recupero dati dal DB
    val thingDB = thingDao.findByID(thingID)
    val thing = Await.result(thingDB, 3 seconds)
    val data=thingDao.findListArray(thing.get)
    //creazione elemento futuro
    val e = new Engine
    val sol=e.getFuture(data)
    // restituisco valore futuro come double facendo una selezione dal risultato
    sol(datatype)
  }


  def futureM(thingID: UUID): Array[Double] = {
    //recupero dati dal DB
    val thingDB = thingDao.findByID(thingID)
    val thing = Await.result(thingDB, 3 seconds)
    val data=thingDao.findListArray(thing.get)
    //creazione elemento futuro
    val e = new Engine
    val sol=e.getFuture(data)
    // restituisco valore futuro come double facendo una selezione dal risultato
    sol
  }
 // @Every("1d")
 def dailyPrediction = Action.async{ implicit request =>
   notificationDao.findAll().flatMap{
     notificationsList =>
        for(notification <- notificationsList){
          if(notification.isThing){
            //prendo il parametro su cui faccio i controlli e lo confronto con i valori min e max delle notifiche
            var parameter = notification.inputType
            thingDao.findByID(notification.thingID.get).flatMap{
              case None =>
                Future.successful(BadRequest(Json.obj("message" -> Messages("thing.notExists"))))
              case Some(thing) =>
                var parameterFind = false
                var count = 0
                for (sensorParameter <- thing.datas(0).sensors if parameterFind == false){
                  if(sensorParameter.sensor == parameter){
                    parameterFind = true
                  }
                  count = count + 1
                }
                var resultFuture = futureV(notification.thingID.get, count)
                if(resultFuture > notification.valMax){
                  val email = Email(
                    "Valori "+parameter+"",
                    "LatexeBiscotti <latexebiscotti@gmail.com>",
                    Seq("Miss TO <"+notification.emailUser+">"),
                    bodyText = Some("Il valore "+parameter+"arriverà a:"+resultFuture+" e il massimo previsto è per"+notification.valMax
                    )
                  )
                  mailer.send(email)
                }
                if(resultFuture < notification.valMin){
                  val email = Email(
                    "Valori "+parameter+"",
                    "LatexeBiscotti <latexebiscotti@gmail.com>",
                    Seq("Miss TO <"+notification.emailUser+">"),
                    bodyText = Some("Il valore "+parameter+"arriverà a:"+resultFuture+" e il minimo previsto è per"+notification.valMin
                    )
                  )
                  mailer.send(email)
                }

                Future.successful(Ok(Json.toJson(thing)))
            }
          }
        }
        Future.successful(Ok(Json.toJson(notificationsList)))
   }
 }



  def index = UserAwareAction.async { implicit request =>
    Future.successful(Ok(Json.obj("test"->"contenuto")))
}
  /**
   * Manages the sign out action.
   */
  def signOut = SecuredAction.async { implicit request =>
    env.eventBus.publish(LogoutEvent(request.identity, request, request2Messages))
    env.authenticatorService.discard(request.authenticator, Ok)
  }

  /**
   * Provides the desired template.
   *
   * @param template The template to provide.
   * @return The template.
   */

}
