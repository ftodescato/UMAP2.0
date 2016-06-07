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

  def getPrediction(labelList: List[Double], measureList: List[Array[Double]]) : Array[Double] = {

    //val conf = new SparkConf().setAppName("LogisticRegressionWithLBFGSExample")
    //val configuration = new SparkConf().setMaster("local[4]").setAppName("Your Application Name");

    val configuration = new SparkConf().setAppName("Simple Application").setMaster("local").set("spark.driver.allowMultipleContexts", "true") ;
    val sc = new SparkContext(configuration)

    // $example on$
    // Load training data in LIBSVM format.

    // questo funziona val data = MLUtils.loadLibSVMFile(sc, "data/sample_libsvm_data.txt")


  /*  val vector1: Vector = Vectors.dense(arr1)
    val vector2: Vector = Vectors.dense(arr2)

    val data: RDD[Vector] = sc.parallelize(Seq(vector1,vector2)) */

    val measureArray = measureList.toArray
    val vecMeasureArray = measureArray.map(Vectors.dense(_))
    val test2:RDD[Vector] = sc.parallelize(vecMeasureArray)

    //Trasforma la lista di label in array
    val labelArray = labelList.toArray

    //Lunghezza degli array
    val length2 = measureArray.length

    //Creo e riempio Array di LabeledPoint per il training
    val trainingArray = new Array[LabeledPoint](length2)

    for (i <- 0 to length2-1) {
      trainingArray(i) = new LabeledPoint(labelArray(i), vecMeasureArray(i))
    }

    //create RDD
    val data: RDD[LabeledPoint] = sc.parallelize(trainingArray)

    // Split data into training (60%) and test (40%).
    val splits = data.randomSplit(Array(0.6, 0.4), seed = 11L)
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
  //  val dv = Vectors.dense(24, 29, 12)

  //  val seriesX = sc.parallelize(dv)

    val prediction = model.predict(test2)

  //  println("PREDIZIONEEEEEEEEEEE" + prediction)

  /*  val yourInputData = MLUtils.loadLibSVMFile(sc, "data/sample_libsvm_data2.txt")
    val res = model.predict(features)
    println("PREDIZIONEEEEEEEEEEEEEEEEE" res)*/


    prediction.collect.toArray
  }
}
