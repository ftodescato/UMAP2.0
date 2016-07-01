package models.daos.chart

import java.util.UUID

import models.Chart
import models.Thing

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
  def findByID(chartID: UUID): Future[Option[Chart]] = {
    collection.find(Json.obj("chartID" -> chartID)).one[Chart]
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

  def findByThingID(thingID: UUID): Future[List[Chart]] = {
    collection.find(Json.obj("thingID" -> thingID)).cursor[Chart]().collect[List]()
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



  def update(chartID: UUID, chart2: Chart) = {
    collection.update(Json.obj("chartID" -> chartID), chart2)
  }


  def remove(chartID: UUID): Future[List[Chart]] = {
    collection.remove(Json.obj("chartID" -> chartID))
    collection.find(Json.obj()).cursor[Chart]().collect[List]()

  }

  def removeByThing(thingID: UUID): Future[List[Chart]] = {
    collection.remove(Json.obj("thingID" -> thingID))
    collection.find(Json.obj()).cursor[Chart]().collect[List]()

  }

}
