package models.daos.chart

import java.util.UUID

import models.Chart

import scala.concurrent.Future

/**
 * Give access to the chart object.
 */
trait ChartDAO {

  /**
   * Finds a Chart by its login info.
   *
   * @param chartName The name of the chart to find.
   * @return The found chart or None if no chart for the given name could be found.
   */


  def findByID(chartID: UUID): Future[Option[Chart]]
  def findAll(): Future[List[Chart]]

  /**
   * Finds a chart by its chart ID.
   *
   * @param chartID The ID of the chart to find.
   * @return The found chart or None if no chart for the given ID could be found.
   */
  def find(chartID: UUID): Future[Option[Chart]]

  /**
   * Saves a chart.
   *
   * @param chart The chart to save.
   * @return The saved chart.
   */
  def save(chart: Chart): Future[Chart]


  def update(chartID: UUID, chart2: Chart)

  def remove(chartID: UUID)
}
