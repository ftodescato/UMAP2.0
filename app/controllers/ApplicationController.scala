package controllers

import javax.inject.Inject
import java.util.UUID
import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models.User
import models.Engine
import models._
import models.daos.thing.ThingDAO
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.mvc.Action
import scala.concurrent.Future
import org.apache.spark.mllib.linalg._
import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.mllib.regression.LabeledPoint


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
  thingDao: ThingDAO,
  val messagesApi: MessagesApi,
//  val engine : Engine,
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
//facade del metodo engine.correlation
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
  // facade dei metodi appartenenti a sumstatistic
  def sumStatistic(thingID: UUID, mv: String, datatype: Int): Double = {
    // recupero list[array[double]] dal db tramite ID
    val thingDB =thingDao.findByID(thingID)
    val thing = Await.result(thingDB, 1 seconds)
    val data=thingDao.findListArray(thing.get)
    // chiamo l'engine per calcolarmi le statistiche sui dati
    val e = new Engine
    val aux: Array[Double] = e.sumStatistic(data, mv)

    //ciclo in cerca dell valore che mi interessa
    var sol:Double=aux(datatype)
    //valore ritornato -inf->+inf
    sol
  }
  // facade per la creaziome del modello degli oggetti a partire dai dati nel DB
  def ModelLogReg(thingID: UUID): LogRegModel ={
    //recupero informazioni dal DB
    val thingDB =thingDao.findByID(thingID)
    val thing = Await.result(thingDB, 1 seconds)
    val label=thingDao.findListLabel(thing.get)
    val data=thingDao.findListArray(thing.get)
    //creo il modello di una thing
    val e = new Engine
    val modello:LogRegModel = e.getLogRegModel(label,data)
    //valore ritornato LogRegModel
    modello
  }
  //NECESSARIO FIX! necessita di una classe di mappatura modello->thing
  def LogReg(thingID: UUID, data: Array[Double]):Double = {
    //recupero informazioni dal DB
    val thingDB =thingDao.findByID(thingID)
    val thing = Await.result(thingDB, 3 seconds)
    val label=thingDao.findListLabel(thing.get)

    val e = new Engine
    // recupero il modello con l'ID della thing
    val modello:LogRegModel = ModelLogReg(thingID)
    // faccio la predizione della nuova label
    val predizione = e.getLogRegPrediction(modello,data)
    // ritorno la label come double
    predizione
  }

  //facade per  la creazione di un elemento futuro
  def futureV(thingID: UUID, datatype:Int): Double = {
    //recupero dati dal DB
    val thingDB =thingDao.findByID(thingID)
    val thing = Await.result(thingDB, 3 seconds)
    val data=thingDao.findListArray(thing.get)
    //creazione elemento futuro
    val e = new Engine
    val sol=e.getFuture(data)
    // restituisco valore futuro come double facendo una selezione dal risultato
    sol(datatype)
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
