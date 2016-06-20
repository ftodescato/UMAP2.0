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
  def correlation = Action.async { implicit request =>
    val a: List[Double] = List(1.2, 2.1, 3.2, 3, 3, 3)
    val b: List[Double] = List(1.2, 2.1, 3.2, 3, 3, 3)
    val e = new Engine
    val aux: Double = e.getCorrelation(a,b)
    Future.successful(Ok(Json.obj("coeff"->aux)))
  }
  // SUMSTATISTIC
  def sumStatistic(mv: String) = Action.async { implicit request =>
    val obs: Array[Double] = Array(1.2, 2, 3, 3, 3, 3)
    val obs2: Array[Double] = Array(0, 1, 0, 3, 3, 3)
    val lista: List[Array[Double]] =List(obs,obs2)
    val e = new Engine
    val aux: Array[Double] = e.sumStatistic(lista, mv)
    Future.successful(Ok(Json.obj("Array"->aux)))
  }

  def NaiveBayes (thingID: UUID) = Action.async { implicit request =>

    val thingDB =thingDao.findByID(thingID)
    val thing = Await.result(thingDB, 1 seconds)
    val label=thingDao.findListLabel(thing.get)
    val data=thingDao.findListArray(thing.get)

    val e = new Engine
    val model:NaiveBayesModel = e.createModel(label,data)

    //val result=e.prediction(data,model)
    //val lista : Array[Double] = Array(24, 16, 34)
    // lista(0)=24
    // lista(1)=16
    // lista(2)=34
    //println(lista)
    //val result:Array[Double] = e.prediction2(lista,model)
    Future.successful(Ok(Json.obj("Array"->data)))
  }

  def ModelLogReg(thingID: UUID): LogRegModel ={
    // val obs: Array[Double] = Array(1.2, 2, 3)
    // val obs2: Array[Double] = Array(0, 1, 0)
    // val obs3: Array[Double] = Array(3.6,5.9,6.7)
    // val obs4: Array[Double] = Array(1.2, 2.1, 3)
    // val obs5: Array[Double] = Array(0.3, 1.1, 0.1)
    // val obs6: Array[Double] = Array(3.7,5.8,6.6)
    // val health: List[Double]= List(0.0,1.0,2.0,0.0,1.0,2.0)
    // val lista: List[Array[Double]] = List(obs,obs2,obs3,obs4,obs5,obs6)
    val thingDB =thingDao.findByID(thingID)
    val thing = Await.result(thingDB, 1 seconds)
    val label=thingDao.findListLabel(thing.get)
    val data=thingDao.findListArray(thing.get)

    val e = new Engine
    val modello:LogRegModel = e.getLogRegModel(label,data)
    modello
  }

  def LogReg(thingID: UUID) = Action.async { implicit request =>
    // val obs: Array[Double] = Array(1.6, 2.1, 3)
    // val obs2: Array[Double] = Array(0, 1, 0)
    // val obs3: Array[Double] = Array(3.6,5.9,6.7)
    // val obs4: Array[Double] = Array(1.2, 2.5, 3)
    // val obs5: Array[Double] = Array(0.3, 1.1, 0.1)
    // val obs6: Array[Double] = Array(3.7,5.6,6.6)
    // val obs7: Array[Double] = Array(1.2, 2, 3)
    // val obs8: Array[Double] = Array(0.1, 1, 0.1)
    // val obs9: Array[Double] = Array(3.6,5.9,6.7)
    //
    // val obs10: Array[Double] = Array(1.6, 2.1, 3)
    // val obs11: Array[Double] = Array(0, 1, 0)
    // val obs12: Array[Double] = Array(3.6,5.9,6.6)
    // val obs13: Array[Double] = Array(1.2, 2.6, 3.1)
    // val obs14: Array[Double] = Array(0.3, 1.1, 0.1)
    // val obs15: Array[Double] = Array(3.7,5.6,6.6)
    // val obs16: Array[Double] = Array(1.2, 2, 3.2)
    // val obs17: Array[Double] = Array(0.2, 1.2, 0.1)
    // val obs18: Array[Double] = Array(3.6,5.8,6.9)
    // val lista: List[Array[Double]] = List(obs,obs2,obs3,obs4,obs5,obs6,obs7,obs8,obs9,obs10,obs11,obs12,obs13,obs14,obs15,obs16,obs17,obs18)

    val thingDB =thingDao.findByID(thingID)
    val thing = Await.result(thingDB, 3 seconds)
    val label=thingDao.findListLabel(thing.get)
    val data=thingDao.findListArray(thing.get)

    val e = new Engine
    val modello:LogRegModel = ModelLogReg(thingID)
    val predizione:Array[Double] = e.getLogRegPrediction(modello,data)
    Future.successful(Ok(Json.obj("Label nel DB"->label,"Array"->predizione)))
  }

  def futureV =Action.async { implicit request =>
      val obs: Array[Double] = Array(1, 4, 2)
      val obs2: Array[Double] = Array(2, 3, 4)
      val obs3: Array[Double] = Array(3,2,6)
      val obs4: Array[Double] = Array(4,1,8)
      val lista: List[Array[Double]] = List(obs,obs2,obs3,obs4)
      val e = new Engine
      val sol=e.getFuture(lista)
      Future.successful(Ok(Json.obj("Array"->sol)))
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
