package models
import play.api.libs.json._
import org.apache.spark.rdd._
import org.apache.spark.mllib.linalg._
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.stat._
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext._
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.mllib.classification.{LogisticRegressionModel, LogisticRegressionWithLBFGS}
import org.apache.spark.mllib.evaluation._
import org.apache.spark.mllib.util._
import models._

class Engine{
  //CORRELATION
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
  //SUMSTATISTIC (FUNZIONI BASE)
  def sumStatistic(lista: List[Array[Double]], mv: String) : Array[Double] = {
    val conf = new SparkConf().setAppName("Simple Application").setMaster("local").set("spark.driver.allowMultipleContexts", "true") ;
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    val toarray = lista.toArray
    val tovectors = toarray.map(Vectors.dense(_))
    val aux: RDD[Vector] = sc.parallelize(tovectors)
    val result:MultivariateStatisticalSummary = Statistics.colStats(aux)
    mv match{
      case "Variance" =>
        result.variance.toArray
      case "Mean" =>
        result.mean.toArray
      case "Max" =>
        result.max.toArray
      case "Min" =>
        result.min.toArray
    }
  }

  //LOGISTIC REGRESSION
  def getLogRegModel(labelList: List[Double], measureList: List[Array[Double]]) : LogRegModel ={
    val configuration = new SparkConf().setAppName("Simple Application").setMaster("local").set("spark.driver.allowMultipleContexts", "true") ;
    val sc = new SparkContext(configuration)
    val measureArray = measureList.toArray
    val vecMeasureArray = measureArray.map(Vectors.dense(_))
    val test2:RDD[Vector] = sc.parallelize(vecMeasureArray)
    val labelArray = labelList.toArray
    val length2 = measureArray.length
    val trainingArray = new Array[LabeledPoint](length2)
    for (i <- 0 to length2-1) {
      trainingArray(i) = new LabeledPoint(labelArray(i), vecMeasureArray(i))
    }
    val data: RDD[LabeledPoint] = sc.parallelize(trainingArray)
    val splits = data.randomSplit(Array(0.6, 0.4), seed = 11L)
    val training = splits(0).cache()
    val test = splits(1)
    val model = new LogisticRegressionWithLBFGS()
      .setNumClasses(10)
      .run(training)
    val predictionAndLabels = test.map { case LabeledPoint(label, features) =>
      val prediction = model.predict(features)
      (prediction, label)    }
    val metrics = new MulticlassMetrics(predictionAndLabels)
    val precision = metrics.precision
    predictionAndLabels.collect().foreach{ point =>  println(point)}
    val savedModel:LogRegModel = new LogRegModel(model.intercept,model.numFeatures,model.numClasses,model.weights)
    savedModel
  }

  def getLogRegPrediction(modello: LogRegModel, measureList: List[Array[Double]]) : Array[Double] = {

    val configuration = new SparkConf().setAppName("Simple Application").setMaster("local").set("spark.driver.allowMultipleContexts", "true") ;
    val sc = new SparkContext(configuration)

    val measureArray = measureList.toArray
    val vecMeasureArray = measureArray.map(Vectors.dense(_))
    val test2:RDD[Vector] = sc.parallelize(vecMeasureArray)
    val loadedModel:LogisticRegressionModel = new LogisticRegressionModel(modello.getWeights,modello.getIntercept,modello.getNumFeatures,modello.getClasses)
    val prediction = loadedModel.predict(test2)
    prediction.collect.toArray
  }

  //NAIVE BAYES
  def createModel (labelList: List[Double], measureList: List[Array[Double]]) : NaiveBayesModel ={
    val configuration = new SparkConf().setAppName("Simple Application").setMaster("local").set("spark.driver.allowMultipleContexts", "true") ;
    val sc = new SparkContext(configuration)
    //Trasforma la lista di misurazioni in array e gli array al suo interno in vector
    val measureArray = measureList.toArray
    val vecMeasureArray = measureArray.map(Vectors.dense(_))

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

    //Split data into training (60%) and test (40%).
    val splits=data.randomSplit(Array(1.0, 0.0), seed = 11L) //type: Array[RDD[]]
    val training = splits(0) //type: RDD

    //Create the model
    val model = NaiveBayes.train(training) //type: NaiveBayesModel
    model
  }

  def prediction (measureList: List[Array[Double]], model: NaiveBayesModel): Array[Double] ={
    val configuration = new SparkConf().setAppName("Simple Application").setMaster("local").set("spark.driver.allowMultipleContexts", "true") ;
    val sc = new SparkContext(configuration)
    //Converte la lista in array e gli array all'interno in vector
    val measureArray = measureList.toArray
    val vecMeasureArray = measureArray.map(Vectors.dense(_))

    //Creo RDD di vector per la predizione
    val test:RDD[Vector] = sc.parallelize(vecMeasureArray)

    //Faccio la predizione
    val result = model.predict(test)

    //Creo un iteratore per ciclare l'RDD
    val it = result.toLocalIterator

    //Lunghezza RDD
    val length = result.count.toInt

    //Creo array per i risultati
    val arrayResult = new Array[Double] (length)

    //Salvo risultati nell'array
    for (i <- 0 to length-1)
    {
      arrayResult(i)= it.next
    }

    // Ritorno Array
    arrayResult
  }

  def prediction2 (data: Array[Double], model: NaiveBayesModel): Double ={
    val configuration = new SparkConf().setAppName("Simple Application").setMaster("local").set("spark.driver.allowMultipleContexts", "true") ;
    val sc = new SparkContext(configuration)
    //Converte la lista in array e gli array all'interno in vector
    val test = Vectors.dense(data)

    //Faccio la predizione
    val result = model.predict(test)
    result
  }
}
