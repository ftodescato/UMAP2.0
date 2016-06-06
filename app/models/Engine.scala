package models
import play.api.libs.json._
import org.apache.spark.rdd._
import org.apache.spark.mllib.linalg._
import org.apache.spark.mllib.stat._
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext._
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
//ALTRI ALGORITMI
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
  //SUMSTATISTIC FUNZIONANTE
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
}
//NAIVE BAYES
class SparkNaiveBayes {

  // val sc = createContext
  // def createContext : SparkContext = {
  //   val conf = new SparkConf(false) // skip loading external settings
  //     .setMaster("local[4]") // run locally with enough threads
  //     .setAppName("firstSparkApp")
  //     .set("spark.logConf", "true")
  //     .set("spark.driver.host", "localhost")
  //
  //   val context = new SparkContext(conf)
  //
  //   context
  // }
  def createModel (labelList: List[Double], measureList: List[Array[Double]]) : NaiveBayesModel ={
    val conf = new SparkConf(false) // skip loading external settings
        .setMaster("local[4]") // run locally with enough threads
        .setAppName("firstSparkApp")
        .set("spark.logConf", "true")
        .set("spark.driver.host", "localhost")
        .set("spark.driver.allowMultipleContexts", "true")

      val sc = new SparkContext(conf)
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
    val conf = new SparkConf(false) // skip loading external settings
        .setMaster("local[4]") // run locally with enough threads
        .setAppName("firstSparkApp")
        .set("spark.logConf", "true")
        .set("spark.driver.host", "localhost")
        .set("spark.driver.allowMultipleContexts", "true")

      val sc = new SparkContext(conf)
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
}
