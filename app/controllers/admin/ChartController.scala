package controllers.admin

import java.util.UUID

import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
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
  chartDao: ChartDAO)
  extends Silhouette[User, JWTAuthenticator] {

  //metodo che dato un UUID di un oggetto restituisce la lista di charts associati
  def showCharts(thingID: UUID) = Action.async { implicit request =>
    val charts = chartDao.findByThingID(thingID)
    charts.flatMap{
      charts =>
      Future.successful(Ok(Json.toJson(charts)))
    }
  }

  //metodo che aggiunge un oggetto Chart al DB
  def addChart = Action.async(parse.json) { implicit request =>
    //richiesta alla form forms.modelAnalyticalData.NewChart
    request.body.validate[NewChart.Data].map { data =>
        //creazione di un nuovo oggetto Chart
        val chart = Chart(
          chartID = UUID.randomUUID(),
          functionName = data.functionName,
          thingID = data.objectID,
          infoDataName = data.parameter
        )
        for{
          //inserimento del nuovo chart nel DB
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
