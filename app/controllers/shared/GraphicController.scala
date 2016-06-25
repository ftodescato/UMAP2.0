package controllers.shared

import java.util.UUID
import javax.inject.Inject

import java.util.{Date, Locale}
import java.text.DateFormat
import java.text.DateFormat._
import java.util.Date

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import forms.notification._
import models._
import models.Chart
import models.Engine
import models.daos.chart._
import models.daos.thing._

import play.api.i18n.{ MessagesApi, Messages }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.Action

import scala.concurrent.Future
import scala.collection.mutable.ListBuffer


class GraphicController @Inject() (
  val messagesApi: MessagesApi,
  chartDao: ChartDAO,
  engine: Engine,
  thingDao: ThingDAO,
  val env: Environment[User, JWTAuthenticator])
  extends Silhouette[User, JWTAuthenticator] {

    def createGraphic(chartID: UUID) = Action.async(parse.json) { implicit request =>
      var futureV = false
      var valueForX: Double = 0
      var valueY = new Array[Double]
      var valueX = new Array[Date]

      val chart = chartDao.findByID(chartID)
      chart.flatMap{
        case None => Future.successful(BadRequest(Json.obj("message" -> Messages("chart.notExists"))))
        case Some (chart) =>
          val thing = thingDao.findByID(chartID)
          thing.flatMap{
            case None => Future.successful(BadRequest(Json.obj("message" -> Messages("thing.notExists"))))
            case Some (thing) =>
              var listMeasurement = thing.datas
              for(measurement <- listMeasurement)
              {
                valueX += measurement.dataTime
                  for(sensors <- measurement.sensors)
                  {
                    if(sensors.sensor == chart.infoDataName){
                        valueY += sensors.value
                    }
                  }
              }
              val listArray = new ListBuffer[Array]
              val functionName = chart.functionName
              functionName match {
                case "Media" => {
                  engine.sumStatistic( ,"Mean")


                }
          }



          }

        Future.successful(Ok(Json.toJson(graphic)))
      }
  }
}
