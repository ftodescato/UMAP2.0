package controllers.admin

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
//import com.mohiva.play.silhouette.api.services.AvatarService
//import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException

import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import forms.modelAnalyticalData._

import models._
import models.daos.chart._
import models.Chart

import play.api.i18n.{ MessagesApi, Messages }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.Action

import scala.concurrent.Future
import scala.collection.mutable.ListBuffer



class ChartController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, JWTAuthenticator],
  //userService: UserService,
  chartDao: ChartDAO
  //userDao: UserDAO,
  //companyDao: CompanyDAO
)
  extends Silhouette[User, JWTAuthenticator] {


  def addChart = Action.async(parse.json) { implicit request =>
    request.body.validate[NewChart.Data].map { data =>
        val chart = Chart(
          chartID = UUID.randomUUID(),
          functionName = data.functionName,
          thingID = data.objectID,
          infoDataName = data.parameter
        )
        for{
          chart <- chartDao.save(chart)
        } yield {
            Ok(Json.obj("ok" -> "ok"))
          }
    }.recoverTotal {
        case error =>
          Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
      }
    }
}
