package models

import org.apache.spark.mllib.classification.{LogisticRegressionModel, LogisticRegressionWithLBFGS}
import org.apache.spark.mllib.linalg.Vector
import java.util.UUID
import play.api.libs.json.Json


case class LogRegModel(
  logRegModelID: UUID,
  thingID: UUID,
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
