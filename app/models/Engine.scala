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
  //crea il modello, lo salva e lo ritorna
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

  // def getLogRegPrediction(modello: LogRegModel, measureList: List[Array[Double]]) : Array[Double] = {
  //
  //   val configuration = new SparkConf().setAppName("Simple Application").setMaster("local").set("spark.driver.allowMultipleContexts", "true") ;
  //   val sc = new SparkContext(configuration)
  //
  //   val measureArray = measureList.toArray
  //   val vecMeasureArray = measureArray.map(Vectors.dense(_))
  //   val test2:RDD[Vector] = sc.parallelize(vecMeasureArray)
  //   val loadedModel:LogisticRegressionModel = new LogisticRegressionModel(modello.getWeights,modello.getIntercept,modello.getNumFeatures,modello.getClasses)
  //   val prediction = loadedModel.predict(test2)
  //   prediction.collect.toArray
  // }

//classifica l'array in input e ritorna la label risultato
  def getLogRegPrediction(modello: LogRegModel, data: Array[Double]) : Double = {

    val configuration = new SparkConf().setAppName("Simple Application").setMaster("local").set("spark.driver.allowMultipleContexts", "true") ;
    val sc = new SparkContext(configuration)
    //val measureArray = measureList.toArray
    val vecMeasureArray = data.map(Vectors.dense(_))
    val test2:RDD[Vector] = sc.parallelize(vecMeasureArray)
    val loadedModel:LogisticRegressionModel = new LogisticRegressionModel(modello.getWeights,modello.getIntercept,modello.getNumFeatures,modello.getClasses)
    val prediction = loadedModel.predict(test2)
    val sol=prediction.collect.toArray
    sol(0)
  }

  def getFuture(lista:List[Array[Double]]):Array[Double] ={
      val times=lista(0).length
      var sol=Array.empty[Double]
      var i:Int=0;
      var a:Double=0;
      for(i<- 0 until times){
        a=getSingleFuture(lista,i)
        sol=sol:+a
      }
      sol
    }

  def getSingleFuture(lista:List[Array[Double]],arraycol:Int):Double ={
      val arraylength:Int =lista(0).length
      val listlength:Int =lista.length
      var listiterator:Int=0;
      var sumxy:Double = 0;
      for(listiterator <- 0 until listlength){
        sumxy=sumxy+((lista(listiterator)(arraycol))*(listiterator+1))
      }
      var sumx:Double =0;
      for(listiterator <- 1 until listlength+1){
        sumx=sumx+listiterator
      }
      var sumy:Double =0;
      for(listiterator<-0 until listlength){
        sumy=sumy+(lista(listiterator)(arraycol))
      }
      var sumsqx:Double=0;
      for(listiterator<- 1 until listlength+1){
        sumsqx=sumsqx+(listiterator*listiterator)
      }
      val slope=(((listlength*sumxy)-(sumx*sumy))/((listlength*sumsqx)-(sumx*sumx)))
      val offset=((sumy-(slope*sumx))/listlength)
      val newvalue=((slope*(listlength+1))+offset)
      newvalue
    }
}
