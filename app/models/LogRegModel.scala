package models

import play.api.libs.json._
import org.apache.spark.mllib.classification.{LogisticRegressionModel, LogisticRegressionWithLBFGS}
import org.apache.spark.mllib.linalg.Vector
import java.util.UUID
import play.api.libs.json.Json
import play.api.libs.json._


case class LogRegModel(
  intercept:Double,
  numFeatures:Int,
  numClasses:Int,
  weights:Array[Double]
){
  def getIntercept: Double ={return intercept}
  def getNumFeatures: Int ={return numFeatures}
  def getClasses: Int ={return numClasses}
  def getWeights: Array[Double] ={return weights}
}

object LogRegModel {

   /**
    * Converts the [LogRegModel] object to Json and vice versa.
    */
   implicit val jsonFormatDetection = Json.format[LogRegModel]
}
