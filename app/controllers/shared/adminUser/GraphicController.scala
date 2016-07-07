package controllers.shared.adminUser

import java.util.UUID
import java.util.Date
import java.util.Calendar

import javax.inject.Inject

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
import controllers._
import play.api.i18n.{ MessagesApi, Messages }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.Action

import scala.concurrent.Future
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.ArrayBuffer


class GraphicController @Inject() (
  val messagesApi: MessagesApi,
  chartDao: ChartDAO,
  engine: ApplicationController,
  thingDao: ThingDAO,
  val env: Environment[User, JWTAuthenticator])
  extends Silhouette[User, JWTAuthenticator] {



    def createGraphic(chartID: UUID) = Action.async { implicit request =>
      var futureV = false
      var valueForX: Double = 0
      var valueY = new ArrayBuffer[Double]()
      var valueX = new ArrayBuffer[Date]()
      val chart = chartDao.findByID(chartID)
      chart.flatMap{
        case None => Future.successful(BadRequest(Json.obj("message" -> Messages("chart.notExists"))))
        case Some (chart) =>
         val thing = thingDao.findByID(chart.thingID)
         thing.flatMap{
           case None => Future.successful(BadRequest(Json.obj("message" -> Messages("thing.notExists"))))
           case Some (thing) =>

            val functionName = chart.functionName
            var function404 = false
            var countForIndex = 0
            var indexFind = false
            var index = 0
            var countForDate = -(1)
            var listMeasurement = thing.datas
            for(measurement <- listMeasurement)
            {
              var date = measurement.dataTime
              valueX += date
              for (sensors <- measurement.sensors){
                  if(sensors.sensor == chart.infoDataName)
                     {
                       if(indexFind == false){
                       index = countForIndex
                       indexFind= true
                       }
                       valueY += sensors.value
                     }
                   countForIndex = countForIndex + 1
                }
                countForDate = countForDate + 1
             }
             functionName match {
                case "Media" =>
                    valueForX = engine.sumStatistic(thing, "Mean", index)
                case "Minimo" =>
                    valueForX = engine.sumStatistic(thing, "Min", index)
                case "Massimo" =>
                    valueForX = engine.sumStatistic(thing, "Max", index)
                case "Varianza" =>
                    valueForX = engine.sumStatistic(thing, "Variance", index)
                case "Future" =>
                    valueForX = engine.futureV(thing, index)
                    futureV = true
                case default =>{
                    function404 = true
                }
              }

              if(function404)  {
                Future.successful(BadRequest(Json.obj("message" -> Messages("function.notExists"))))
              }
              else{
                var lastDateMeasurement = thing.datas(countForDate).dataTime
                var nextDate= Calendar.getInstance()
                nextDate.setTime(lastDateMeasurement)
                nextDate.add(Calendar.DAY_OF_MONTH, 1)
                valueX += nextDate.getTime()
                 val graphic = Graphic(
                   futureV = futureV,
                   valuesY = valueY.toArray,
                   valuesX = valueX.toArray,
                   resultFunction = valueForX
                 )
                Future.successful(Ok(Json.toJson(graphic)))

              }
      }
    }
  }
}
