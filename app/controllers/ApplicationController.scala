package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models.User
import models.Engine
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.mvc.Action
import scala.concurrent.Future
import org.apache.spark.mllib.linalg._

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
def sumStatistic(mv: String) = Action.async { implicit request =>
  val obs: List[Double] = List(1.2, 2, 3, 3, 3, 3)
  val obs2: List[Double] = List(0, 1, 0, 3, 3, 3)
  val e = new Engine
  val aux: Array[Double] = e.sumStatistic(obs, obs2, mv)
  Future.successful(Ok(Json.obj("Array"->aux)))
}

//NAIVE BAYES
// def bayesengine = Action.async { implicit request =>
//   val data = Vectors.dense(1.0, 0.0, 3.0)
//   val e = new Engine
//   val aux: Vector = e.getBayes(data)
//   val temp: Array[Double] =aux.toArray
//   Future.successful(Ok(Json.obj("coeff"->temp)))
// }
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
