package models

import java.util.UUID

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
//import models._
import collection.breakOut

class Engine{
  //metodo per la correlazione
  def getCorrelation(a: Array[Double], b: Array[Double]) : Double = {
    val conf = new SparkConf().setAppName("Simple Application").setMaster("local").set("spark.driver.allowMultipleContexts", "true") ;
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    val seriesX: RDD[Double] = sc.parallelize(a)
    val seriesY: RDD[Double] = sc.parallelize(b)
    //calcolo correlazione tra valori e la loro R
    val correlation: Double = Statistics.corr(seriesX, seriesY, "pearson")
    correlation
  }
  //calcolo della R di un insieme di punti per la correlazione
  def getPointsOnR(lista:Array[Double]): Array[Double]={
    var pointsOnR =Array.empty[Double]
    val listlength = lista.length
    var listiterator:Int =0
    var sumxy:Double =0
    for (listiterator <- 0 until listlength){
      sumxy=sumxy+(lista(listiterator)*(listiterator+1))
    }
    var sumx:Double =0;
    for(listiterator <- 1 until listlength+1){
      sumx=sumx+listiterator
    }
    var sumy:Double =0;
    for(listiterator<-0 until listlength){
      sumy=sumy+lista(listiterator)
    }
    var sumsqx:Double=0;
    for(listiterator<- 1 until listlength+1){
      sumsqx=sumsqx+(listiterator*listiterator)
    }
    val slope=(((listlength*sumxy)-(sumx*sumy))/((listlength*sumsqx)-(sumx*sumx)))
    val offset=((sumy-(slope*sumx))/listlength)
    for (listiterator<-0 until listlength){
      pointsOnR=pointsOnR:+((slope*(listiterator+1))+offset)
    }
    pointsOnR
  }

  //SUMSTATISTIC (FUNZIONI BASE)
  def sumStatistic(lista: List[Array[Double]], mv: String) : Array[Double] = {
    val conf = new SparkConf().setAppName("Simple Application").setMaster("local").set("spark.driver.allowMultipleContexts", "true") ;
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    // imposto i dati per la funzione spark
    val toarray = lista.toArray
    val tovectors = toarray.map(Vectors.dense(_))
    val aux: RDD[Vector] = sc.parallelize(tovectors)
    // applico la funzione
    val result:MultivariateStatisticalSummary = Statistics.colStats(aux)
    mv match{
      //restituisco la varianza
      case "Variance" =>
        result.variance.toArray
      //la media
      case "Mean" =>
        result.mean.toArray
      //il massimo
      case "Max" =>
        result.max.toArray
      //il minimo
      case "Min" =>
        result.min.toArray
    }
  }

  //LOGISTIC REGRESSION
  //crea il modello e lo ritorna
  def getLogRegModel(thingID: UUID, labelList: List[Double], measureList: List[Array[Double]]) : LogRegModel ={
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
    val savedModel:LogRegModel = new LogRegModel(UUID.randomUUID(),thingID,model.intercept,model.numFeatures,model.numClasses,model.weights.toArray)
    savedModel
  }


  //applica il modello di una thing ad una nuova misurazione e restituisce la sua label
  def getLogRegPrediction(modello: LogRegModel, data: Array[Double]) : Double = {

    val configuration = new SparkConf().setAppName("Simple Application").setMaster("local").set("spark.driver.allowMultipleContexts", "true") ;
    val sc = new SparkContext(configuration)

    val vecMeasureArray = data.map(Vectors.dense(_))
    val test2:RDD[Vector] = sc.parallelize(vecMeasureArray)
    val weights = Vectors.dense(modello.getWeights)
    val loadedModel:LogisticRegressionModel = new LogisticRegressionModel(weights,modello.getIntercept,modello.getNumFeatures,modello.getClasses)
    val prediction = loadedModel.predict(test2)
    val sol=prediction.collect.toArray
    sol(0)
  }
  // calcola una possibile misurazione futura
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
  // metodo chiamato da getFuture per generare i singoli dati di una misurazione
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
      //y=slope*x+offset (y=ax+b)
      val newvalue=((slope*(listlength+1))+offset)
      newvalue
    }
}
