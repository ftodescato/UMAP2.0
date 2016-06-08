package models
import scalax.chart.api._

class MyChartApp extends App with scalax.chart.module.Charting {
  def plot () {

  val data = for (i <- 1 to 5) yield (i,i)
  val chart = XYLineChart(data, title = "My Chart of Some Points")
  chart.show()
  }
}
