package models
import play.api.libs.json._
import org.apache.spark.rdd._
import org.apache.spark.mllib.linalg._
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext._
import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}

class Engine{
  def getCorrelation(a: List[Double], b: List[Double]) : Double = {
    val conf = new SparkConf().setAppName("Simple Application").setMaster("local").set("spark.driver.allowMultipleContexts", "true") ;
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    //val df = sqlContext.read.json(Json.toJson(a).toString())
    //sc.parallelize(Json.toJson(a).toString())
    val seriesX: RDD[Double] = sc.parallelize(a)
    val seriesY: RDD[Double] = sc.parallelize(b)
    val correlation: Double = Statistics.corr(seriesX, seriesY, "pearson")
    correlation
  }
  def getBayes(data: Vector) : Vector = {
    val conf = new SparkConf().setAppName("NaiveBayesExample")
    val sc = new SparkContext(conf)

    val model = NaiveBayes.train(data, lambda = 1.0, modelType = "multinomial")

    val prediction:Vector = model.predict(data)
    prediction
  }
}
