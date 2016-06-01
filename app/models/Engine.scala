package models
import play.api.libs.json._
import org.apache.spark.rdd._
import org.apache.spark.mllib.linalg._
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext._

import org.apache.spark.{SparkConf, SparkContext}
// $example on$
import org.apache.spark.mllib.classification.{LogisticRegressionModel, LogisticRegressionWithLBFGS}
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.rdd._


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

  def getPrediction() : Unit = {

    //val conf = new SparkConf().setAppName("LogisticRegressionWithLBFGSExample")
    //val configuration = new SparkConf().setMaster("local[4]").setAppName("Your Application Name");

    val configuration = new SparkConf().setAppName("Simple Application").setMaster("local").set("spark.driver.allowMultipleContexts", "true") ;
    val sc = new SparkContext(configuration)

    // $example on$
    // Load training data in LIBSVM format.
    val data = MLUtils.loadLibSVMFile(sc, "data/sample_libsvm_data.txt")

    // Split data into training (60%) and test (40%).
    val splits = data.randomSplit(Array(0.6, 0.24), seed = 11L)
    val training = splits(0).cache()
    val test = splits(1)

    // Run training algorithm to build the model
    val model = new LogisticRegressionWithLBFGS()
      .setNumClasses(10)
      .run(training)

    // Compute raw scores on the test set.
    val predictionAndLabels = test.map { case LabeledPoint(label, features) =>
      val prediction = model.predict(features)
      (prediction, label)    }

    // Get evaluation metrics.
   val metrics = new MulticlassMetrics(predictionAndLabels)
   val precision = metrics.precision
   println("Precision = " + precision)

   predictionAndLabels.collect().foreach{ point =>  println(point)
}

    // Save and load model
  /*model.save(sc, "target/tmp/scalaLogisticRegressionWithLBFGSModel")
    val sameModel = LogisticRegressionModel.load(sc,
      "target/tmp/scalaLogisticRegressionWithLBFGSModel")*/
    // $example off$*/
    val dv = Vectors.dense(24, 15, 34)
  //  val seriesX = sc.parallelize(dv)
    val prediction = model.predict(dv)

    println("PREDIZIONEEEEEEEEEEE" + prediction)

  /*  val yourInputData = MLUtils.loadLibSVMFile(sc, "data/sample_libsvm_data2.txt")
    val res = model.predict(features)
    println("PREDIZIONEEEEEEEEEEEEEEEEE" res)*/

    sc.stop()
  }
}
