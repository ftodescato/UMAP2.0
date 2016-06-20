package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models.User
import models.Engine
import models._
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.mvc.Action
import scala.concurrent.Future
import org.apache.spark.mllib.linalg._
import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.mllib.regression.LabeledPoint

/**
 * The basic application controller.
 *
 * @param messagesApi The Play messages API.
 * @param env The Silhouette environment.
 * @param socialProviderRegistry The social provider registry.
 */
class ApplicationController @Inject() (
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

  def NaiveBayes = Action.async { implicit request =>
    val obs: Array[Double] = Array(1.2, 2, 3)
    val obs2: Array[Double] = Array(0, 1, 0)
    val obs3: Array[Double] = Array(1.2,2,3)
    val health: List[Double]= List(0.0,1.0,2.0)
    val lista: List[Array[Double]] = List(obs,obs2,obs3)
    val e = new Engine
    val aux:NaiveBayesModel = e.createModel(health,lista)
    val temp:Array[Double] = e.prediction(lista,aux)
    Future.successful(Ok(Json.obj("Array"->temp)))
  }
  def ModelLogReg(): LogRegModel ={
    val obs: Array[Double] = Array(1.2, 2, 3)
    val obs2: Array[Double] = Array(0, 1, 0)
    val obs3: Array[Double] = Array(1.2,2,3)
    val health: List[Double]= List(0.0,1.0,0.0)
    val lista: List[Array[Double]] = List(obs,obs2,obs3)
    val e = new Engine
    val modello:LogRegModel = e.getLogRegModel(health,lista)
    modello
  }
  def LogReg = Action.async { implicit request =>
    val obs: Array[Double] = Array(1.2, 2, 3)
    val obs2: Array[Double] = Array(0, 1, 0)
    val obs3: Array[Double] = Array(1.2,2,3)
    val lista: List[Array[Double]] = List(obs,obs2,obs3)
    val e = new Engine
    val modello:LogRegModel = ModelLogReg()
    val predizione:Array[Double] = e.getLogRegPrediction(modello,lista)
    Future.successful(Ok(Json.obj("Array"->predizione)))
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
