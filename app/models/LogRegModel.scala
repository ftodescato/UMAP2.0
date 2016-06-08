package models

import play.api.libs.json._
import org.apache.spark.mllib.classification.{LogisticRegressionModel, LogisticRegressionWithLBFGS}
import org.apache.spark.mllib.linalg.Vector

class LogRegModel(i:Double,nf:Int,nc:Int,w:Vector){
  val intercept:Double = i
  val numFeatures:Int = nf
  val numClasses:Int = nc
  val  weights:Vector = w
  def getIntercept: Double ={return intercept}
  def getNumFeatures: Int ={return numFeatures}
  def getClasses: Int ={return numClasses}
  def getWeights: Vector ={return weights}
  override def toString: String ={return "intercept"+intercept+" numFeatures"+numFeatures+" numClasses"+numClasses}
}
