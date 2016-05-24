package models.daos.chart

import java.util.UUID

import models.Chart

import scala.collection.mutable
import scala.concurrent.Future

import javax.inject.Inject
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.api._

import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection._

/**
 * Give access to the chart object.
 */
class ChartDAOImpl @Inject() (db : DB) extends ChartDAO {

  def collection: JSONCollection = db.collection[JSONCollection]("chart")

  /**
   * Finds a chart by its name.
   *
   * @param chartName The name of the chart to find.
   * @return The found chart or None if no chart for the given name could be found.
   */
  def find(chartName: String): Future[Option[Chart]] = {
    collection.find(Json.obj("chartName" -> chartName)).one[Chart]
  }

  def findAll(): Future[List[Chart]] = {
    collection.find(Json.obj()).cursor[Chart]().collect[List]()
  }

  /**
   * Finds a chart by its chart ID.
   *
   * @param chartID The ID of the chart to find.
   * @return The found chart or None if no chart for the given ID could be found.
   */
  def find(chartID: UUID) : Future[Option[Chart]] = {
    collection.find(Json.obj("chartID" -> chartID)).one[Chart]
  }

  /**
   * Saves a chart.
   *
   * @param chart The chart to save.
   * @return The saved chart.
   */
  def save(chart: Chart) = {
    collection.insert(chart)
    Future.successful(chart)
  }

}
