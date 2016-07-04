package controllers.shared.adminUser

import java.util.UUID
import javax.inject.Inject

import java.util.{Date, Locale}
import java.text.DateFormat
import java.text.DateFormat._
import java.util.Date
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.util.Calendar

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
      var valueX = new ArrayBuffer[String]()
      //var arrayDouble = Array.empty[Double]
      val chart = chartDao.findByID(chartID)
      chart.flatMap{
        case None => Future.successful(BadRequest(Json.obj("message" -> Messages("chart.notExists"))))
        case Some (chart) =>
         val thing = thingDao.findByID(chart.thingID)
         thing.flatMap{
           case None => Future.successful(BadRequest(Json.obj("message" -> Messages("thing.notExists"))))
           case Some (thing) =>
           val functionName = chart.functionName
           var countForIndex = 0
           var indexFind = false
           var index = 0
           var countForDate = -(1)
           var listMeasurement = thing.datas
           for(measurement <- listMeasurement)
           {
             var date = measurement.dataTime
             valueX += date.toString()
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
                    valueForX = engine.sumStatistic(thing.thingID, "Mean", index)
                case "Minimo" =>
                    valueForX = engine.sumStatistic(thing.thingID, "Min", index)
                case "Massimo" =>
                    valueForX = engine.sumStatistic(thing.thingID, "Max", index)
                case "Varianza" =>
                    valueForX = engine.sumStatistic(thing.thingID, "Variance", index)
                case "Future" =>
                    valueForX = engine.futureV(thing.thingID, index)
                }
            var lastDateMeasurement = thing.datas(countForDate).dataTime
            //  val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            //  val oldDate = LocalDate.parse(lastDateMeasurement, formatter)
            //  var secondLastDateMeasurement = thing.datas(countForDate - 1).dataTime.toString()
            //  val newDate = LocalDate.parse(secondLastDateMeasurement, formatter)
            //  var differenceForDate = newDate.toEpochDay() - oldDate.toEpochDay()
            //  var nextDate = lastDateMeasurement + differenceForDate
            //valueX += lastDateMeasurement.toString()

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
